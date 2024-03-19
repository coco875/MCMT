package net.himeki.mcmt.serdes.filter;

import net.himeki.mcmt.serdes.ISerDesHookType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.World;

public class VanillaFilter implements ISerDesFilter {

    @Override
    public void serialise(Runnable task, Object obj, BlockPos bp, World w, ISerDesHookType hookType) {
        task.run();
    }

    @Override
    public ClassMode getModeOnline(Class<?> c) {
        if (c.getName().startsWith("net.minecraft")) {
            return ClassMode.WHITELIST;
        }
        return ClassMode.UNKNOWN;
    }

}
