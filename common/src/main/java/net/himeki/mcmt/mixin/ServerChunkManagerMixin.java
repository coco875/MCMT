package net.himeki.mcmt.mixin;

import com.mojang.datafixers.util.Either;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkAccessManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

import net.himeki.mcmt.DebugHookTerminator;
import net.himeki.mcmt.ParallelProcessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;


@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin extends ChunkManager {

    @Shadow
    @Final
    public ServerChunkCache.MainThreadExecutor mainThreadExecutor;

    @Shadow
    @Final
    ServerLevel world;

    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void preChunkTick(CallbackInfo ci) {
        ParallelProcessor.preChunkTick(this.world);
    }

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickChunk(Lnet/minecraft/world/chunk/LevelChunk;I)V"))
    private void overwriteTickChunk(ServerLevel serverWorld, LevelChunk chunk, int randomTickSpeed) {
        ParallelProcessor.callTickChunks(serverWorld, chunk, randomTickSpeed);
    }


    @Redirect(method = {"getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", "getLevelChunk"}, at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerChunkCache;serverThread:Ljava/lang/Thread;", opcode = Opcodes.GETFIELD))
    private Thread overwriteServerThread(ServerChunkCache mgr) {
        return Thread.currentThread();
    }

    @Redirect(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;visit(Ljava/lang/String;)V"))
    private void overwriteProfilerVisit(Profiler instance, String s) {
        if (ParallelProcessor.shouldThreadChunks())
            return;
        else instance.visit("getChunkCacheMiss");
    }

    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;runTasks(Ljava/util/function/BooleanSupplier;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void callCompletableFutureHook(int x, int z, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir, Profiler profiler, long chunkPos, CompletableFuture<Either<Chunk, ChunkHolder.ChunkLoadingFailure>> i) {
        DebugHookTerminator.chunkLoadDrive(this.mainThreadExecutor, i::isDone, (ServerChunkCache) (Object) this, i, chunkPos);
    }

}