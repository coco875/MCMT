package net.himeki.mcmt;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class MCMTExpectPlatform {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String[] getLoadedMods() {
        throw new AssertionError();
    }
}
