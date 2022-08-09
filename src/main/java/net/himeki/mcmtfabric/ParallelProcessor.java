package net.himeki.mcmtfabric;

import net.himeki.mcmtfabric.config.BlockEntityLists;
import net.himeki.mcmtfabric.config.GeneralConfig;
import net.himeki.mcmtfabric.serdes.SerDesHookTypes;
import net.himeki.mcmtfabric.serdes.SerDesRegistry;
import net.himeki.mcmtfabric.serdes.filter.ISerDesFilter;
import net.himeki.mcmtfabric.serdes.pools.PostExecutePool;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ParallelProcessor {

    private static final Logger LOGGER = LogManager.getLogger();

    static Phaser worldPhaser;

    static ConcurrentHashMap<ServerWorld, Phaser> sharedPhasers = new ConcurrentHashMap<>();
    static ExecutorService ex;
    static MinecraftServer mcs;
    static AtomicBoolean isTicking = new AtomicBoolean();
    static AtomicInteger threadID = new AtomicInteger();

    public static void setupThreadPool(int parallelism) {
        threadID = new AtomicInteger();
        final ClassLoader cl = MCMT.class.getClassLoader();
        ForkJoinPool.ForkJoinWorkerThreadFactory fjpf = p -> {
            ForkJoinWorkerThread fjwt = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(p);
            fjwt.setName("MCMT-Pool-Thread-" + threadID.getAndIncrement());
            regThread("MCMT", fjwt);
            fjwt.setContextClassLoader(cl);
            return fjwt;
        };
        ex = new ForkJoinPool(parallelism, fjpf, null, true);
    }

    /**
     * Creates and sets up the thread pool
     */
    static {
        // Must be static here due to class loading shenanagins
        // setupThreadPool(4);
    }

    static Map<String, Set<Thread>> mcThreadTracker = new ConcurrentHashMap<String, Set<Thread>>();

    // Statistics
    public static AtomicInteger currentWorlds = new AtomicInteger();
    public static AtomicInteger currentEnts = new AtomicInteger();
    public static AtomicInteger currentTEs = new AtomicInteger();
    public static AtomicInteger currentEnvs = new AtomicInteger();

    //Operation logging
    public static Set<String> currentTasks = ConcurrentHashMap.newKeySet();

    public static void regThread(String poolName, Thread thread) {
        mcThreadTracker.computeIfAbsent(poolName, s -> ConcurrentHashMap.newKeySet()).add(thread);
    }

    public static boolean isThreadPooled(String poolName, Thread t) {
        return mcThreadTracker.containsKey(poolName) && mcThreadTracker.get(poolName).contains(t);
    }

    public static boolean serverExecutionThreadPatch(MinecraftServer ms) {
        return isThreadPooled("MCMT", Thread.currentThread());
    }

    static long tickStart = 0;
    static GeneralConfig config;

    public static void preTick(int size, MinecraftServer server) {
        config = MCMT.config; // Load when config are loaded. Static loads before config update.
        if (!config.disabled && !config.disableWorld) {
            if (worldPhaser != null) {
                LOGGER.warn("Multiple servers?");
                return;
            } else {
                tickStart = System.nanoTime();
                isTicking.set(true);
                worldPhaser = new Phaser(size + 1);
                mcs = server;
            }
        }
    }

    public static void callTick(ServerWorld serverworld, BooleanSupplier hasTimeLeft, MinecraftServer server) {
        if (config.disabled || config.disableWorld) {
            try {
                serverworld.tick(hasTimeLeft);
            } catch (Exception e) {
                throw e;
            }
            return;
        }
        if (mcs != server) {
            LOGGER.warn("Multiple servers?");
            config.disabled = true;
            serverworld.tick(hasTimeLeft);
            return;
        } else {
            String taskName = null;
            if (config.opsTracing) {
                taskName = "WorldTick: " + serverworld.toString() + "@" + serverworld.hashCode();
                currentTasks.add(taskName);
            }
            String finalTaskName = taskName;
            ex.execute(() -> {
                try {
                    currentWorlds.incrementAndGet();
                    serverworld.tick(hasTimeLeft);
                } finally {
                    worldPhaser.arriveAndDeregister();
                    currentWorlds.decrementAndGet();
                    if (config.opsTracing) currentTasks.remove(finalTaskName);
                }
            });
        }
    }

    public static long[] lastTickTime = new long[32];
    public static int lastTickTimePos = 0;
    public static int lastTickTimeFill = 0;

    public static void postTick(MinecraftServer server) {
        if (!config.disabled && !config.disableWorld) {
            if (mcs != server) {
                LOGGER.warn("Multiple servers?");
                return;
            } else {
                worldPhaser.arriveAndAwaitAdvance();
                isTicking.set(false);
                worldPhaser = null;
                //PostExecute logic
                Deque<Runnable> queue = PostExecutePool.POOL.getQueue();
                Iterator<Runnable> qi = queue.iterator();
                while (qi.hasNext()) {
                    Runnable r = qi.next();
                    r.run();
                    qi.remove();
                }
                lastTickTime[lastTickTimePos] = System.nanoTime() - tickStart;
                lastTickTimePos = (lastTickTimePos + 1) % lastTickTime.length;
                lastTickTimeFill = Math.min(lastTickTimeFill + 1, lastTickTime.length - 1);
            }
        }
    }

    public static void preChunkTick(ServerWorld world) {
        Phaser phaser; // Keep a party throughout 3 ticking phases
        if (!config.disabled && !config.disableEnvironment) {
            phaser = new Phaser(2);
        } else {
            phaser = new Phaser(1);
        }
        sharedPhasers.put(world, phaser);
    }

    public static void callTickChunks(ServerWorld world, WorldChunk chunk, int k) {
        if (config.disabled || config.disableEnvironment) {
            world.tickChunk(chunk, k);
            return;
        }
        String taskName = null;
        if (config.opsTracing) {
            taskName = "EnvTick: " + chunk.toString() + "@" + chunk.hashCode();
            currentTasks.add(taskName);
        }
        String finalTaskName = taskName;
        sharedPhasers.get(world).register();
        ex.execute(() -> {
            try {
                currentEnvs.incrementAndGet();
                world.tickChunk(chunk, k);
            } finally {
                if (config.opsTracing) currentTasks.remove(finalTaskName);
                sharedPhasers.get(world).arriveAndDeregister();
                currentEnvs.decrementAndGet();
            }
        });
    }

    public static void postChunkTick(ServerWorld world) {
        if (!config.disabled && !config.disableEnvironment) {
            var phaser = sharedPhasers.get(world);
            phaser.arriveAndDeregister();
            phaser.arriveAndAwaitAdvance();
        }
    }

    public static void preEntityTick(ServerWorld world) {
        if (!config.disabled && !config.disableEntity) sharedPhasers.get(world).register();
    }

    public static void callEntityTick(Consumer<Entity> tickConsumer, Entity entityIn, ServerWorld serverworld) {
        if (config.disabled || config.disableEntity) {
            tickConsumer.accept(entityIn);
            return;
        }
        String taskName = null;
        if (config.opsTracing) {
            taskName = "EntityTick: " + /*entityIn.toString() + KG: Wayyy too slow. Maybe for debug but needs to be done via flag in that circumstance */ "@" + entityIn.hashCode();
            currentTasks.add(taskName);
        }
        String finalTaskName = taskName;
        sharedPhasers.get(serverworld).register();
        ex.execute(() -> {
            try {
                final ISerDesFilter filter = SerDesRegistry.getFilter(SerDesHookTypes.EntityTick, entityIn.getClass());
                currentEnts.incrementAndGet();
                if (filter != null) {
                    filter.serialise(() -> tickConsumer.accept(entityIn), entityIn, entityIn.getBlockPos(), serverworld, SerDesHookTypes.EntityTick);
                } else {
                    tickConsumer.accept(entityIn);
                }
            } finally {
                if (config.opsTracing) currentTasks.remove(finalTaskName);
                sharedPhasers.get(serverworld).arriveAndDeregister();
                currentEnts.decrementAndGet();
            }
        });
    }

    public static void postEntityTick(ServerWorld world) {
        if (!config.disabled && !config.disableEntity) {
            var phaser = sharedPhasers.get(world);
            phaser.arriveAndDeregister();
            phaser.arriveAndAwaitAdvance();
        }
    }

    public static void preBlockEntityTick(ServerWorld world) {
        if (!config.disabled && !config.disableTileEntity) sharedPhasers.get(world).register();
    }

    public static void callBlockEntityTick(BlockEntityTickInvoker tte, World world) {
        if ((world instanceof ServerWorld) && tte instanceof WorldChunk.WrappedBlockEntityTickInvoker && (((WorldChunk.WrappedBlockEntityTickInvoker) tte).wrapped instanceof WorldChunk.DirectBlockEntityTickInvoker<?>)) {
            if (config.disabled || config.disableTileEntity) {
                tte.tick();
                return;
            }
            if (((WorldChunk.DirectBlockEntityTickInvoker<?>) ((WorldChunk.WrappedBlockEntityTickInvoker) tte).wrapped).blockEntity instanceof PistonBlockEntity) {
                tte.tick();
                return;
            }
            String taskName = null;
            if (config.opsTracing) {
                taskName = "TETick: " + tte.toString() + "@" + tte.hashCode();
                currentTasks.add(taskName);
            }
            String finalTaskName = taskName;
            sharedPhasers.get(world).register();
            ex.execute(() -> {
                try {
                    final ISerDesFilter filter = SerDesRegistry.getFilter(SerDesHookTypes.TETick, ((WorldChunk.WrappedBlockEntityTickInvoker) tte).wrapped.getClass());
                    currentTEs.incrementAndGet();
                    if (filter != null) filter.serialise(tte::tick, tte, tte.getPos(), world, SerDesHookTypes.TETick);
                    else tte.tick();
                } catch (Exception e) {
                    System.err.println("Exception ticking TE at " + tte.getPos());
                    e.printStackTrace();
                } finally {
                    if (config.opsTracing) currentTasks.remove(finalTaskName);
                    sharedPhasers.get(world).arriveAndDeregister();
                    currentTEs.decrementAndGet();
                }
            });
        } else tte.tick();
    }

    public static boolean filterTE(BlockEntityTickInvoker tte) {
        boolean isLocking = false;
        if (BlockEntityLists.teBlackList.contains(tte.getClass())) {
            isLocking = true;
        }
        // Apparently a string starts with check is faster than Class.getPackage; who knew (I didn't)
        if (!isLocking && config.chunkLockModded && !tte.getClass().getName().startsWith("net.minecraft.block.entity.")) {
            isLocking = true;
        }
        if (isLocking && BlockEntityLists.teWhiteList.contains(tte.getClass())) {
            isLocking = false;
        }
        if (tte instanceof PistonBlockEntity) {
            isLocking = true;
        }
        return isLocking;
    }

    public static void postBlockEntityTick(ServerWorld world) {
        if (!config.disabled && !config.disableTileEntity) {
            var phaser = sharedPhasers.get(world);
            phaser.arriveAndDeregister();
            phaser.arriveAndAwaitAdvance();
        }
    }

    public static boolean shouldThreadChunks() {
        return !MCMT.config.disableMultiChunk;
    }

}
