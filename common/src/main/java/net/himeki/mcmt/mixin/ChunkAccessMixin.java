package net.himeki.mcmt.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin {

    @Shadow
    @Final
    @Mutable
    private Map<BlockPos, BlockEntity> blockEntities =  ConcurrentCollections.newHashMap();

}
