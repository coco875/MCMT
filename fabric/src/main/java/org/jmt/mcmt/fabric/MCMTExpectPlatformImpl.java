package org.jmt.mcmt.fabric;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class MCMTExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance()
            .getConfigDir()
            .toAbsolutePath()
            .normalize();
    }
}
