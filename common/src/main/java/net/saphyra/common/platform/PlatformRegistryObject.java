package net.saphyra.common.platform;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/** Shared PlatformRegistryObject implementation for consuming mods. */
public interface PlatformRegistryObject<T> extends Supplier<T> {

    @Override
    T get();

    ResourceLocation getId();
}
