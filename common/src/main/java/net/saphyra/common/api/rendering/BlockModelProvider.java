package net.saphyra.common.api.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adapts a copied BlockState to Minecraft's vanilla rendering system.
 *
 * <p>This class acts as a bridge between the custom system and the
 * vanilla model renderer. It does not perform any custom rendering.</p>
 */
public final class BlockModelProvider {

    /**
     * Prevent instantiation.
     */
    private BlockModelProvider() {}

    /**
     * Returns the vanilla BakedModel corresponding to the supplied BlockState.
     *
     * <p>If the supplied BlockState is {@code null} or represents air,
     * the vanilla AIR model is returned.</p>
     */
    public static @NotNull BakedModel getModel(@Nullable BlockState state) {

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

                if (state == null || state.isAir()) return dispatcher.getBlockModel(Blocks.AIR.defaultBlockState());

                return dispatcher.getBlockModel(state);
    }

}
