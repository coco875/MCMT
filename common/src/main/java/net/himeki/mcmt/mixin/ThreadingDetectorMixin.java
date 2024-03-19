package net.himeki.mcmt.mixin;

import net.minecraft.util.ThreadingDetector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.Semaphore;

@Mixin(ThreadingDetector.class)
public abstract class ThreadingDetectorMixin<T> {

    @Shadow
    @Final
    @Mutable
    private Semaphore semaphore = new Semaphore(255);
}
