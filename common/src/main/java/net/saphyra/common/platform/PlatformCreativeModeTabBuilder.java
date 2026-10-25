package net.saphyra.common.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/** Shared PlatformCreativeModeTabBuilder implementation for consuming mods. */
public interface PlatformCreativeModeTabBuilder {

    PlatformCreativeModeTabBuilder title(Component title);

    PlatformCreativeModeTabBuilder icon(Supplier<ItemStack> icon);

    PlatformCreativeModeTabBuilder displayItems(CreativeModeTab.DisplayItemsGenerator generator);

    PlatformCreativeModeTabBuilder withSearchBar();

    /** The supplied tab must appear before the tab being built. */
    PlatformCreativeModeTabBuilder withTabsBefore(ResourceLocation before);

    CreativeModeTab build();
}
