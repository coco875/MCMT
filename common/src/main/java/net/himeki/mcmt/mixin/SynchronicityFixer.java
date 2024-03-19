package net.himeki.mcmt.mixin;

import net.minecraft.world.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.world.entity.ai.pathing.PathNodeNavigator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {PathNodeNavigator.class,BirdPathNodeMaker.class/*,Object2LongOpenHashMap.class,ReferenceOpenHashSet.class,ReferenceArrayList.class,Int2ObjectOpenHashMap.class, Long2ObjectOpenHashMap.class, LongLinkedOpenHashSet.class, ObjectOpenCustomHashSet.class, Long2LongOpenHashMap.class, Long2ObjectLinkedOpenHashMap.class*/},
        targets = {"it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$ValueIterator", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$KeySet", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$KeyIterator",
                "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapEntrySet", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$EntryIterator", "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapIterator",
                "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap$MapEntry", "it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap$FastEntryIterator", "it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap$MapIterator",
                "it.unimi.dsi.fastutil.objects.ReferenceArrayList$Spliterator", "it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet$SetIterator", "it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap", "net.minecraft.world.entity.ai.pathing.BirdPathNodeMaker"
                , "net.minecraft.world.entity.ai.pathing.PathNodeNavigator", "net.minecraft.server.level.ChunkTicketManager", "it.unimi.dsi.fastutil.ints.IntArrayList", "it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap", "it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap$MapIterator"
                , "net.minecraft.server.level.ThreadedAnvilChunkStorage", "it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap", "it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet", "net.minecraft.world.entity.EntityTrackingSection"//,"it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet"
                , "net.minecraft.world.tick.ChunkTickScheduler", "net.minecraft.world.poi.PointOfInterestStorage", "net.minecraft.world.entity.ai.pathing.Path", "net.minecraft.world.entity.ai.pathing.EntityNavigation"
                , "it.unimi.dsi.fastutil.objects.ObjectOpenHashSet", "net.minecraft.server.level.ServerEntityManager.Listener", "it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap"
                , "net.minecraft.util.math.random.CheckedRandom", "net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess", /*"com.simibubi.create.content.contraptions.KineticNetwork"
                , "aztech.modern_industrialization.machines.recipe.MachineRecipeType", "aztech.modern_industrialization.machines.multiblocks.world.ChunkPosMultiMap"
                , "com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler", "appeng.helpers.ForgeEnergyAdapter", "net.fabricmc.fabric.impl.transfer.transaction.TransactionManagerImpl$TransactionImpl"
                , "appeng.me.service.EnergyService", "dev.technici4n.moderndynamics.network.NetworkManager", "com.simibubi.create.foundation.advancement.CriterionTriggerBase" */

        },priority = 50000)
public class SynchronicityFixer {
}
