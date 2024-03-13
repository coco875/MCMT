package org.jmt.mcmt.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;

import org.jmt.mcmt.parallelised.ConcurrentCollections;
import org.jmt.mcmt.parallelised.fastutil.Int2ObjectConcurrentHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityIndex.class)
public abstract class EntityIndexMixin<T extends EntityLike> {
    @Shadow
    @Final
    @Mutable
    private Int2ObjectMap<T> idToEntity;

    @Shadow
    @Final
    @Mutable
    private Map<UUID, T> uuidToEntity = ConcurrentCollections.newHashMap();

    @Inject(method = "<init>",at = @At("TAIL"))
    private void replaceConVars(CallbackInfo ci)
    {
        idToEntity = new Int2ObjectConcurrentHashMap<>();
    }

}
