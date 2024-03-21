package net.himeki.mcmt.mixin;

import java.util.Set;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import net.himeki.mcmt.parallelised.fastutil.ConcurrentLongLinkedOpenHashSet;
import net.himeki.mcmt.parallelised.fastutil.Long2ObjectOpenConcurrentHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;

@Mixin(DistanceManager.class)
public abstract class DistanceManagerMixin {

    @Shadow
    @Final
    @Mutable
	Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenConcurrentHashMap<>();
	   
    @Shadow
    @Final
    @Mutable
    Set<ChunkHolder> chunksToUpdateFutures = ConcurrentCollections.newHashSet();

    @Shadow
    @Final
    @Mutable
    LongSet ticketsToRelease = new ConcurrentLongLinkedOpenHashSet();
}