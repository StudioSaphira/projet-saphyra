package net.saphyra.neoforge.platform;

import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.platform.PlatformRegistryObject;

import java.util.function.Supplier;

/** Shared NeoForgePlatformRegistryObject implementation for consuming mods. */
public final class NeoForgePlatformRegistryObject<T> implements PlatformRegistryObject<T> {

    private final Supplier<? extends T> supplier;
    private final Supplier<ResourceLocation> idSupplier;

    public NeoForgePlatformRegistryObject(
            Supplier<? extends T> supplier, Supplier<ResourceLocation> idSupplier) {
        this.supplier = supplier;
        this.idSupplier = idSupplier;
    }

    @Override
    public T get() {
        return supplier.get();
    }

    @Override
    public ResourceLocation getId() {
        return idSupplier.get();
    }
}
