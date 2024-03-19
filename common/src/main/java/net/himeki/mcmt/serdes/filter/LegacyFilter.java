package net.himeki.mcmt.serdes.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.himeki.mcmt.config.BlockEntityLists;
import net.himeki.mcmt.serdes.SerDesRegistry;
import net.himeki.mcmt.serdes.pools.ChunkLockPool;
import net.himeki.mcmt.serdes.pools.ISerDesPool;
import net.himeki.mcmt.serdes.pools.ISerDesPool.ISerDesOptions;
import net.himeki.mcmt.serdes.ISerDesHookType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LegacyFilter implements ISerDesFilter {

    ISerDesPool clp;
    ISerDesOptions config;

    @Override
    public void init() {
        clp = SerDesRegistry.getOrCreatePool("LEGACY", ChunkLockPool::new);
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("range", "1");
        config = clp.compileOptions(cfg);
    }

    @Override
    public void serialise(Runnable task, Object obj, BlockPos bp, Level w, ISerDesHookType hookType) {
        clp.serialise(task, obj, bp, w, config);
    }

    @Override
    public Set<Class<?>> getTargets() {
        return BlockEntityLists.teBlackList;
    }

    @Override
    public Set<Class<?>> getWhitelist() {
        return BlockEntityLists.teWhiteList;
    }

}
