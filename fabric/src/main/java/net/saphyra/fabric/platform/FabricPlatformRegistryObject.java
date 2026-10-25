package net.saphyra.fabric.platform;

import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.platform.PlatformRegistryObject;

/** Shared FabricPlatformRegistryObject implementation for consuming mods. */
public final class FabricPlatformRegistryObject<T> implements PlatformRegistryObject<T> {

    private final T value;
    private final ResourceLocation id;

    public FabricPlatformRegistryObject(T value, ResourceLocation id) {
        this.value = value;
        this.id = id;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
