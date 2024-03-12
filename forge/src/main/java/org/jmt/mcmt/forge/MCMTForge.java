package org.jmt.mcmt.forge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.google.common.collect.Multimap;
import java.util.function.Consumer;

import org.jmt.mcmt.MCMT;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimaps;

@Mod(MCMT.MOD_ID)
public class MCMTForge {
    private static final Map<String, IEventBus> EVENT_BUS_MAP = Collections.synchronizedMap(new HashMap<>());
    private static final Multimap<String, Consumer<IEventBus>> ON_REGISTERED = Multimaps.synchronizedMultimap(LinkedListMultimap.create());

    public MCMTForge() {
        // Submit our event bus to let architectury register our content on the right time
        registerModEventBus(MCMT.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MCMT.init();
    }

    public static void registerModEventBus(String modId, IEventBus bus) {
        if (EVENT_BUS_MAP.putIfAbsent(modId, bus) != null) {
            throw new IllegalStateException("Can't register event bus for mod '" + modId + "' because it was previously registered!");
        }
        
        for (Consumer<IEventBus> consumer : ON_REGISTERED.get(modId)) {
            consumer.accept(bus);
        }
    }
}
