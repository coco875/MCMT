package net.himeki.mcmt;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.himeki.mcmt.commands.StatsCommand;
import net.himeki.mcmt.config.GeneralConfig;
import net.himeki.mcmt.jmx.JMXRegistration;
import net.himeki.mcmt.serdes.SerDesRegistry;

public class MCMT {
    public static final String MOD_ID = "mcmt";
    public static final Logger LOGGER = LogManager.getLogger();
    public static GeneralConfig config;
    
    public static void init() {        
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initializing MCMT...");
        ConfigHolder<GeneralConfig> holder = AutoConfig.register(GeneralConfig.class, Toml4jConfigSerializer::new);
        holder.registerLoadListener((manager, data) -> {
            holder.getConfig().loadTELists();
            return InteractionResult.SUCCESS;
        });
        holder.load();  // Load again to run loadTELists() handler
        config = holder.getConfig();

        if (System.getProperty("jmt.mcmt.jmx") != null) {
            JMXRegistration.register();
        }

        StatsCommand.runDataThread();
        SerDesRegistry.init();

        MCMT.LOGGER.info("MCMT Setting up threadpool...");
        ParallelProcessor.setupThreadPool(GeneralConfig.getParallelism());
    }
}
