package net.saphyra.neoforge.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.saphyra.common.platform.PlatformCreativeModeTabBuilder;

import java.util.function.Supplier;

/** Shared NeoForgeCreativeModeTabBuilder implementation for consuming mods. */
public final class NeoForgeCreativeModeTabBuilder implements PlatformCreativeModeTabBuilder {

    private final CreativeModeTab.Builder builder;

    public NeoForgeCreativeModeTabBuilder() {
        this.builder = CreativeModeTab.builder();
    }

    @Override
    public PlatformCreativeModeTabBuilder title(Component title) {
        builder.title(title);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder icon(Supplier<ItemStack> icon) {
        builder.icon(icon);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder displayItems(
            CreativeModeTab.DisplayItemsGenerator generator) {
        builder.displayItems(generator);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder withSearchBar() {
        builder.withSearchBar();
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder withTabsBefore(ResourceLocation before) {
        builder.withTabsBefore(before);
        return this;
    }

    @Override
    public CreativeModeTab build() {
        return builder.build();
    }
}
