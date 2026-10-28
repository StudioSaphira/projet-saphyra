package net.saphyra.common.api.rendering;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.geometry.GeometryFace;
import org.jetbrains.annotations.Nullable;

/** Supplies block-local faces, or null when the provider does not own the selected block. */
@FunctionalInterface
public interface OutlineProvider {

    @Nullable GeometryFace[] faces(BlockAndTintGetter level, BlockPos pos, BlockState state);
}
