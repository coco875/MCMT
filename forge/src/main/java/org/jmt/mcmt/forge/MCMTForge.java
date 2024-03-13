package org.jmt.mcmt.forge;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.jmt.mcmt.MCMT;
import org.jmt.mcmt.commands.ConfigCommand;
import org.jmt.mcmt.commands.StatsCommand;

import com.mojang.brigadier.CommandDispatcher;

@Mod(MCMT.MOD_ID)
public class MCMTForge {

    public MCMTForge() {
        MCMT.init();
        // Submit our event bus to let architectury register our content on the right time
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        StatsCommand.resetAll();
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
    	CommandDispatcher<ServerCommandSource> commandDispatcher = event.getDispatcher();
        ConfigCommand.register(commandDispatcher);
    }
}
