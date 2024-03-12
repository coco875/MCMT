package org.jmt.mcmt.fabric;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jmt.mcmt.MCMT;
import org.jmt.mcmt.config.GeneralConfig;
import org.jmt.mcmt.fabric.commands.ConfigCommand;
import org.jmt.mcmt.fabric.commands.StatsCommand;
import org.jmt.mcmt.fabric.jmx.JMXRegistration;
import org.jmt.mcmt.fabric.serdes.SerDesRegistry;

public class MCMTFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MCMT.init();

        if (System.getProperty("jmt.mcmt.jmx") != null) {
            JMXRegistration.register();
        }

        StatsCommand.runDataThread();
        SerDesRegistry.init();


        MCMT.LOGGER.info("MCMT Setting up threadpool...");
        ParallelProcessor.setupThreadPool(GeneralConfig.getParallelism());


        // Listener reg begin
        ServerLifecycleEvents.SERVER_STARTED.register(server -> StatsCommand.resetAll());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConfigCommand.register(dispatcher));

    }
}
