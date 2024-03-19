package net.himeki.mcmt.mixin;

import net.minecraft.util.ClassInstanceMultiMap;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.AbstractCollection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;

@Mixin(ClassInstanceMultiMap.class)
public abstract class ClassInstanceMultiMapMixin<T> extends AbstractCollection<T> {
    @Shadow
    @Final
    @Mutable
    private Map<Class<?>, List<T>> byClass = new ConcurrentHashMap<>();

    @Shadow
    @Final
    @Mutable
    private List<T> allInstances = new CopyOnWriteArrayList<>();

    @ModifyArg(method = "m_13537_", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private <T> Collector<T, ?, List<T>> overwriteCollectToList(Collector<T, ?, List<T>> collector) {
        return ConcurrentCollections.toList();
    }
}