package net.himeki.mcmt.mixin;

import net.minecraft.Util;

import net.himeki.mcmt.ParallelProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

@Mixin(Util.class)
public abstract class UtilMixin {

    // lambda$makeExecutor$3
    @Inject(method = "m_201861_", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ForkJoinWorkerThread;setName(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void registerThread(String string, ForkJoinPool forkJoinPool, CallbackInfoReturnable<ForkJoinWorkerThread> cir, ForkJoinWorkerThread forkJoinWorkerThread) {
        ParallelProcessor.regThread(string, forkJoinWorkerThread);
    }
}