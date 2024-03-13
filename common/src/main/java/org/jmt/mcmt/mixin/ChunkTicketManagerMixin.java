package org.jmt.mcmt.mixin;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;

import org.jmt.mcmt.parallelised.ConcurrentCollections;
import org.jmt.mcmt.parallelised.fastutil.ConcurrentLongLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ChunkTicketManager.class)
public abstract class ChunkTicketManagerMixin {

    @Shadow
    @Final
    @Mutable
    Set<ChunkHolder> chunkHolders = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    LongSet chunkPositions = new ConcurrentLongLinkedOpenHashSet();
}
