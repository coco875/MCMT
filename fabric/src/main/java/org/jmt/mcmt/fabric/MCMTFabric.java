package org.jmt.mcmt.fabric;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.jmt.mcmt.MCMT;
import org.jmt.mcmt.commands.ConfigCommand;
import org.jmt.mcmt.commands.StatsCommand;

public class MCMTFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MCMT.init();

        // Listener reg begin
        ServerLifecycleEvents.SERVER_STARTED.register(server -> StatsCommand.resetAll());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConfigCommand.register(dispatcher));

    }
}
