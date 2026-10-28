package net.saphyra.common.api.lighting;

import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.platform.LightingPlatform;
import net.saphyra.common.platform.PlatformServices;

/** Public lighting facade: consuming mods declare values, while Saphyra owns collection and rendering. */
public final class SaphyraLighting {

    private SaphyraLighting() {}

    public static void registerBlock(Block block, Function<BlockState, LightEmission> profile) {
        LightProfiles.registerBlock(block, profile);
    }

    public static void registerItem(Item item, Function<ItemStack, LightEmission> profile) {
        LightProfiles.registerItem(item, profile);
    }

    /** Client-only resolution after the loader has installed its lighting platform. */
    public static LightEmission itemEmission(ItemStack stack) {
        return PlatformServices.extension(LightingPlatform.class).itemEmission(stack);
    }
}
