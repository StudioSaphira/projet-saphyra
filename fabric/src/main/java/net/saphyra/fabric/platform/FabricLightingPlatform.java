package net.saphyra.fabric.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.lighting.*;
import net.saphyra.common.platform.LightingPlatform;

/** Resolves RGB values and vanilla fallback emission on Fabric. */
public final class FabricLightingPlatform implements LightingPlatform {

    @Override
    public LightEmission blockEmission(BlockState state, BlockGetter level, BlockPos pos) {
        LightEmission custom = LightProfiles.block(state);
        return custom != null ? custom : LightEmission.white(state.getLightEmission());
    }

    @Override
    public LightEmission itemEmission(ItemStack stack) {
        if (stack.isEmpty()) return LightEmission.NONE;
        LightEmission custom = LightProfiles.item(stack);
        if (custom != null) return custom;
        if (stack.getItem() instanceof BlockItem item) {
            BlockState state = item.getBlock().defaultBlockState();
            custom = LightProfiles.block(state);
            return custom != null ? custom : LightEmission.white(state.getLightEmission());
        }
        return LightEmission.NONE;
    }
}
