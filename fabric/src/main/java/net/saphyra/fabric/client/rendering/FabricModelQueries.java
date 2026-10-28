package net.saphyra.fabric.client.rendering;

import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Reusable client rendering utility, independent of consuming mods. */
public final class FabricModelQueries {

    private FabricModelQueries() {
    }

    public static @NotNull List<BakedQuad> getCopiedQuads(
            @NotNull BakedModel copiedModel,
            @NotNull BlockState copiedState,
            @Nullable Direction direction,
            @NotNull RandomSource random
    ) {
        List<BakedQuad> quads =
                copiedModel.getQuads(
                        copiedState,
                        direction,
                        random
                );

        if (!quads.isEmpty()) {
            return quads;
        }

        return copiedModel.getQuads(
                copiedState,
                null,
                random
        );
    }

    public static @NotNull List<BakedQuad> findMatchingCopiedQuads(
            @NotNull BakedModel copiedModel,
            @NotNull BlockState copiedState,
            @Nullable Direction geometryDirection,
            @NotNull BakedQuad geometryQuad,
            @NotNull RandomSource random
    ) {
        Direction direction =
                geometryDirection != null
                        ? geometryDirection
                        : geometryQuad.getDirection();

        return getCopiedQuads(
                copiedModel,
                copiedState,
                direction,
                random
        );
    }
}
