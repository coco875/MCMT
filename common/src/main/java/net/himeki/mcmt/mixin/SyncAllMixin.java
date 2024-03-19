package net.himeki.mcmt.mixin;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import net.minecraft.world.level.gameevent.EuclideanGameEventDispatcher;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {BinaryHeap.class, LevelChunkTicks.class, DynamicGraphMinFixedPoint.class, PathNavigation.class, ThreadingDetector.class, EuclideanGameEventDispatcher.class, ChunkStatus.class})
public class SyncAllMixin {
}
