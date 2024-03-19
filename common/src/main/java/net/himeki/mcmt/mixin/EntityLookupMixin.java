package net.himeki.mcmt.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntityAccess;

import net.himeki.mcmt.parallelised.ConcurrentCollections;
import net.himeki.mcmt.parallelised.fastutil.Int2ObjectConcurrentHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityLookup.class)
public abstract class EntityLookupMixin<T extends EntityAccess> {
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
