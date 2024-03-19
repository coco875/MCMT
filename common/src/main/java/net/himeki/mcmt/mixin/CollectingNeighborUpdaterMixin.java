package net.himeki.mcmt.mixin;

import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(CollectingNeighborUpdater.class)
public abstract class CollectingNeighborUpdaterMixin implements NeighborUpdater {

    @Shadow
    @Final
    @Mutable
    List<CollectingNeighborUpdater.NeighborUpdates> pending = new CopyOnWriteArrayList<>();


}
