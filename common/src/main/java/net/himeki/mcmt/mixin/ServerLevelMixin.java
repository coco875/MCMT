package net.himeki.mcmt.mixin;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.himeki.mcmt.ParallelProcessor;
import net.himeki.mcmt.parallelised.ConcurrentCollections;
import net.himeki.mcmt.parallelised.ParaServerChunkProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements WorldGenLevel {

    ConcurrentLinkedDeque<BlockEventData> syncedBlockEventDataCLinkedQueue = new ConcurrentLinkedDeque<BlockEventData>();

    @Shadow
    @Final
    @Mutable
    Set<Mob> loadedMobs = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    private ObjectLinkedOpenHashSet<BlockEventData> syncedBlockEventDataQueue = null;

    @Shadow
    @Final
    EntityTickList entityList;
    ServerLevel thisWorld = (ServerLevel) (Object) this;

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/server/level/ServerChunkCache"))
    private ServerChunkCache overwriteServerChunkCache(ServerLevel world, LevelStorageSource.LevelStorageAccess session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, int simulationDistance, boolean dsync, ChunkProgressListener worldGenerationProgressListener, ChunkStatusUpdateListener chunkStatusChangeListener, Supplier<DimensionDataStorage> persistentStateManagerFactory) {
        return new ParaServerChunkProvider(world, session, dataFixer, structureTemplateManager, workerExecutor, chunkGenerator, viewDistance, simulationDistance, dsync, worldGenerationProgressListener, chunkStatusChangeListener, persistentStateManagerFactory);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 5))
    private void postChunkTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ParallelProcessor.postChunkTick(thisWorld);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 2))
    private void preEntityTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ParallelProcessor.preEntityTick(thisWorld);
    }

    @Redirect(method = "method_31420", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickEntity(Ljava/util/function/Consumer;Lnet/minecraft/entity/Entity;)V"))
    private void overwriteEntityTicking(ServerLevel instance, Consumer<Entity> consumer, Entity entity) {
        ParallelProcessor.callEntityTick(consumer, entity, thisWorld);
    }

    @Redirect(method = "addSyncedBlockEventData", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
    private boolean overwriteQueueAdd(ObjectLinkedOpenHashSet<BlockEventData> objectLinkedOpenHashSet, Object object) {
        return syncedBlockEventDataCLinkedQueue.add((BlockEventData) object);
    }

    @Redirect(method = "clearUpdatesInArea", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeIf(Ljava/util/function/Predicate;)Z"))
    private boolean overwriteQueueRemoveIf(ObjectLinkedOpenHashSet<BlockEventData> objectLinkedOpenHashSet, Predicate<BlockEventData> filter) {
        return syncedBlockEventDataCLinkedQueue.removeIf(filter);
    }

    @Redirect(method = "processSyncedBlockEventDatas", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"))
    private boolean overwriteEmptyCheck(ObjectLinkedOpenHashSet<BlockEventData> objectLinkedOpenHashSet) {
        return syncedBlockEventDataCLinkedQueue.isEmpty();
    }

    @Redirect(method = "processSyncedBlockEventDatas", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"))
    private Object overwriteQueueRemoveFirst(ObjectLinkedOpenHashSet<BlockEventData> objectLinkedOpenHashSet) {
        return syncedBlockEventDataCLinkedQueue.removeFirst();
    }

    @Redirect(method = "processSyncedBlockEventDatas", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;addAll(Ljava/util/Collection;)Z"))
    private boolean overwriteQueueAddAll(ObjectLinkedOpenHashSet<BlockEventData> instance, Collection<? extends BlockEventData> c) {
        return syncedBlockEventDataCLinkedQueue.addAll(c);
    }

    @Redirect(method = "updateListeners", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;duringListenerUpdate:Z", opcode = Opcodes.PUTFIELD))
    private void skipSendBlockUpdatedCheck(ServerLevel instance, boolean value) {

    }
}
