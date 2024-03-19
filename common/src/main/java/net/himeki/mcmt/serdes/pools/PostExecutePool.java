package net.himeki.mcmt.serdes.pools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.World;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PostExecutePool implements ISerDesPool {

    public static final PostExecutePool POOL = new PostExecutePool();

    private PostExecutePool() {}

    Deque<Runnable> runnables = new ConcurrentLinkedDeque<Runnable>();

    @Override
    public void serialise(Runnable task, Object o, BlockPos bp, World w, ISerDesOptions options) {
        runnables.add(task);
    }

    public Deque<Runnable> getQueue() {
        return runnables;
    }


}
