package org.jmt.mcmt;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmt.mcmt.config.GeneralConfig;

public class MCMT {
    public static final String MOD_ID = "mcmt";
    public static final Logger LOGGER = LogManager.getLogger();
    public static GeneralConfig config;
    
    public static void init() {        
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initializing MCMTFabric...");
        ConfigHolder<GeneralConfig> holder = AutoConfig.register(GeneralConfig.class, Toml4jConfigSerializer::new);
        holder.registerLoadListener((manager, data) -> {
            holder.getConfig().loadTELists();
            return ActionResult.SUCCESS;
        });
        holder.load();  // Load again to run loadTELists() handler
        config = holder.getConfig();
    }
}
