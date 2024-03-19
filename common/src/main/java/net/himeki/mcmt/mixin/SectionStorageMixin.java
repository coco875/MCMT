package net.himeki.mcmt.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.world.level.chunk.storage.SectionStorage;

import net.himeki.mcmt.parallelised.fastutil.ConcurrentLongLinkedOpenHashSet;
import net.himeki.mcmt.parallelised.fastutil.Long2ObjectConcurrentHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SectionStorage.class)
public abstract class SectionStorageMixin<R> implements AutoCloseable {
    @Shadow
    @Final
    @Mutable
    private Long2ObjectMap<Optional<R>> loadedElements = new Long2ObjectConcurrentHashMap<>();

    @Shadow
    @Final
    @Mutable
    private LongLinkedOpenHashSet unsavedElements = new ConcurrentLongLinkedOpenHashSet();
}
