package net.himeki.mcmt.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.Level;

import net.himeki.mcmt.DebugHookTerminator;
import net.himeki.mcmt.ParallelProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> implements CommandSource, AutoCloseable {
    @Shadow
    public abstract ServerLevel getOverworld();

    @Shadow
    @Final
    private Map<ResourceKey<Level>, ServerLevel> worlds;

    public MinecraftServerMixin(String string) {
        super(string);
    }

    // @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"))
    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getCommandFunctionManager()Lnet/minecraft/server/function/CommandFunctionManager;"))
    private void preTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ParallelProcessor.preTick(this.worlds.size(), (MinecraftServer) (Object) this);
    }

    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
    private void postTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ParallelProcessor.postTick((MinecraftServer) (Object) this);
    }

    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void overwriteTick(ServerLevel serverWorld, BooleanSupplier shouldKeepTicking) {
        ParallelProcessor.callTick(serverWorld, shouldKeepTicking, (MinecraftServer) (Object) this);
    }

    @Redirect(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnThread()Z"))
    private boolean onServerExecutionThreadPatch(MinecraftServer minecraftServer) {
        return ParallelProcessor.serverExecutionThreadPatch(minecraftServer);
    }

    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;getTotalChunksLoadedCount()I"))
    private int initialChunkCountBypass(ServerChunkCache instance) {
        if (DebugHookTerminator.isBypassLoadTarget())
            return 441;
        int loaded = this.getOverworld().getChunkSource().getTickingGenerated();
        return Math.min(loaded, 441); // Maybe because multi loading caused overflow
    }

}

