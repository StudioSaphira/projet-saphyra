package net.saphyra.fabric.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.saphyra.common.platform.PlatformCreativeModeTabBuilder;

import java.util.function.Supplier;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

public final class FabricCreativeModeTabBuilder implements PlatformCreativeModeTabBuilder {

    private final CreativeModeTab.Builder builder;
    private boolean searchBar;
    private ResourceLocation predecessor;

    public FabricCreativeModeTabBuilder() {
        this.builder = FabricItemGroup.builder();
    }

    @Override
    public PlatformCreativeModeTabBuilder title(
            Component title
    ) {
        builder.title(title);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder icon(
            Supplier<ItemStack> icon
    ) {
        builder.icon(icon);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder displayItems(
            CreativeModeTab.DisplayItemsGenerator generator
    ) {
        builder.displayItems(generator);
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder withSearchBar() {
        searchBar = true;
        builder.backgroundTexture(ResourceLocation.withDefaultNamespace(
                "textures/gui/container/creative_inventory/tab_item_search.png"));
        return this;
    }

    @Override
    public PlatformCreativeModeTabBuilder withTabsBefore(
            ResourceLocation before
    ) {
        predecessor = before;
        return this;
    }

    @Override
    public CreativeModeTab build() {
        CreativeModeTab tab = builder.build();
        FabricTabSettings.configure(tab, searchBar, predecessor);
        return tab;
    }
}
