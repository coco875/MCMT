package net.himeki.mcmt.mixin;

import net.minecraft.world.entity.ai.pathing.EntityNavigation;
import net.minecraft.world.entity.ai.pathing.PathMinHeap;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.light.LevelPropagator;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {PathMinHeap.class, ChunkTickScheduler.class, LevelPropagator.class, EntityNavigation.class, LockHelper.class, SimpleGameEventDispatcher.class, ChunkStatus.class})
public class SyncAllMixin {
}
