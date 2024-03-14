package org.jmt.mcmt.forge;

import java.nio.file.Path;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

public class MCMTExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static String[] getLoadedMods() {
        return ModList.get().applyForEachModContainer(mc -> mc.getModId() + ":" + mc.getModInfo().getVersion().toString()).toArray(String[]::new);
    }
}
