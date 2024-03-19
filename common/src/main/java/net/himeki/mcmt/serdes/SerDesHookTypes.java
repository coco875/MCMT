package net.himeki.mcmt.serdes;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;

public enum SerDesHookTypes implements ISerDesHookType {
    EntityTick(Entity.class),
    TETick(BlockEntityTicker.class);

    Class<?> clazz;

    SerDesHookTypes(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public Class<?> getSuperclass() {
        return clazz;
    }


}
