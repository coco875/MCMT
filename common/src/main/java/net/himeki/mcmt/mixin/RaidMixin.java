package net.himeki.mcmt.mixin;

import java.util.Map;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

@Mixin(Raid.class)
public class RaidMixin {

    @Mutable
    @Shadow
    @Final
    Map<Integer, Raider> groupToLeaderMap = ConcurrentCollections.newHashMap();
}
