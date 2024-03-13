package org.jmt.mcmt.forge;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

public class MCMTExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
