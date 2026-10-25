package net.saphyra.fabric.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.saphyra.fabric.platform.FabricTabSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

/** Applies shared creative-tab behavior for every mod using Saphyra. */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeTabSearchMixin
        extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow private static CreativeModeTab selectedTab;
    @Shadow private EditBox searchBox;
    @Shadow private float scrollOffs;

    protected CreativeTabSearchMixin(
            CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    // Reuse vanilla text input, focus, clearing, resizing and keyboard handling.
    // The actual tab remains CATEGORY so its entries still populate global search.
    @Redirect(
            method = {"selectTab", "charTyped", "keyPressed", "refreshCurrentTabContents"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;"))
    private CreativeModeTab.Type saphyra$searchableType(CreativeModeTab tab) {
        return FabricTabSettings.hasSearchBar(tab) ? CreativeModeTab.Type.SEARCH : tab.getType();
    }

    @Inject(method = "refreshSearchResults", at = @At("HEAD"), cancellable = true)
    private void saphyra$searchWithinTab(CallbackInfo ci) {
        if (!FabricTabSettings.hasSearchBar(selectedTab)) return;
        String query = searchBox.getValue().strip().toLowerCase(Locale.ROOT);
        menu.items.clear();
        for (ItemStack stack : selectedTab.getDisplayItems()) {
            boolean matches;
            if (query.startsWith("#")) {
                String tagQuery = query.substring(1);
                matches =
                        stack.getTags()
                                .anyMatch(tag -> tag.location().toString().contains(tagQuery));
            } else {
                matches =
                        stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)
                                || BuiltInRegistries.ITEM
                                        .getKey(stack.getItem())
                                        .toString()
                                        .contains(query);
            }
            if (matches) menu.items.add(stack);
        }
        scrollOffs = 0.0F;
        menu.scrollTo(0.0F);
        ci.cancel();
    }
}
