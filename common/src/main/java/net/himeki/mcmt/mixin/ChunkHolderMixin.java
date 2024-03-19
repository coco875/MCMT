package net.himeki.mcmt.mixin;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.lighting.LevelLightEngine;

import net.himeki.mcmt.parallelised.fastutil.ConcurrentShortHashSet;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {

    @Mutable
    @Shadow
    @Final
    private ShortSet[] blockUpdatesBySection;

    // @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    @Inject(method = "<init>", at = @At(value = "TAIL", target = "Lnet/minecraft/server/level/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;"))
    private void overwriteShortSet(ChunkPos pos, int level, LevelHeightAccessor world, LevelLightEngine lightingProvider, ChunkHolder.LevelChangeListener levelUpdateListener, ChunkHolder.PlayerProvider playersWatchingChunkProvider, CallbackInfo ci) {
        this.blockUpdatesBySection = new ConcurrentShortHashSet[world.getSectionsCount()];
    }

    @Redirect(method = "markForBlockUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ChunkHolder;blockUpdatesBySection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;", args = "array=set"))
    private void setBlockUpdatesBySection(ShortSet[] array, int index, ShortSet value) {
        array[index] = new ConcurrentShortHashSet();
    }
}
