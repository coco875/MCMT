package org.jmt.mcmt.fabric;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class MCMTExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance()
            .getConfigDir()
            .toAbsolutePath()
            .normalize();
    }

    public String[] getLoadedMods() {
        return FabricLoader.getInstance().getAllMods().stream().map(ModContainer::getMetadata).map(info -> info.getId() + ":" + info.getVersion().toString()).toArray(String[]::new);
    }
}
