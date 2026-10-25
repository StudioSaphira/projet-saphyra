package net.saphyra.common.platform;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface PlatformRegistryObject<T>
        extends Supplier<T> {

    @Override
    T get();

    ResourceLocation getId();
}