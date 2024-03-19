package net.himeki.mcmt.mixin;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import net.himeki.mcmt.parallelised.fastutil.ConcurrentLongLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(DistanceManager.class)
public abstract class DistanceManagerMixin {

    @Shadow
    @Final
    @Mutable
    Set<ChunkHolder> chunkHolders = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    LongSet chunkPositions = new ConcurrentLongLinkedOpenHashSet();
}
