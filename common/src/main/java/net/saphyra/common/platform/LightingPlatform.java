package net.saphyra.common.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.lighting.LightEmission;

/** Resolves consumer RGB profiles and loader-aware vanilla fallbacks for the client light engine. */
public interface LightingPlatform {

    LightEmission blockEmission(BlockState state, BlockGetter level, BlockPos pos);

    /** An unregistered BlockItem inherits its block profile or vanilla emission; other items emit nothing. */
    LightEmission itemEmission(ItemStack stack);
}
