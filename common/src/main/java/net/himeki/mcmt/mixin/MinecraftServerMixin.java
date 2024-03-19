package net.himeki.mcmt.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.commands.CommandOutput;
import net.minecraft.server.level.ServerChunkManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;

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
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements CommandOutput, AutoCloseable {
    @Shadow
    public abstract ServerLevel getOverworld();

    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerLevel> worlds;

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

    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void overwriteTick(ServerLevel serverWorld, BooleanSupplier shouldKeepTicking) {
        ParallelProcessor.callTick(serverWorld, shouldKeepTicking, (MinecraftServer) (Object) this);
    }

    @Redirect(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnThread()Z"))
    private boolean onServerExecutionThreadPatch(MinecraftServer minecraftServer) {
        return ParallelProcessor.serverExecutionThreadPatch(minecraftServer);
    }

    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int initialChunkCountBypass(ServerChunkManager instance) {
        if (DebugHookTerminator.isBypassLoadTarget())
            return 441;
        int loaded = this.getOverworld().getChunkManager().getLoadedChunkCount();
        return Math.min(loaded, 441); // Maybe because multi loading caused overflow
    }

}

