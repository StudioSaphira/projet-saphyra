package net.saphyra.neoforge.client.rendering;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.saphyra.common.api.rendering.BlockModelProvider;
import net.saphyra.neoforge.client.rendering.NeoForgeModelQueries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Retextures supplied models without knowing the consumer block or material slots. */
public final class NeoForgeQuadRetexturing {

    private NeoForgeQuadRetexturing() {}

    /**
     * <h1>TINT</h1>
     * ================================================================
     * Encodes the tint index of a material slot.
     */
    public static int encodeTintIndex(
            int tintIndex,
            int tintOffset
    ) {
        if (tintIndex < 0) {
            return tintIndex;
        }

        return tintIndex + tintOffset;
    }

    /**
     * <h1>QUAD REMAPPING</h1>
     * ================================================================
     * Remaps the UV coordinates of a material quad from its original
     * sprite to the sprite of the copied block.
     *
     * <p>The geometry remains unchanged. Only its texture mapping
     * is replaced.</p>
     */
    public static @NotNull BakedQuad remapQuad(
            @NotNull BakedQuad sourceQuad,
            @NotNull TextureAtlasSprite sourceSprite,
            @NotNull BakedQuad copiedQuad,
            int tintOffset
    ) {
        TextureAtlasSprite targetSprite =
                copiedQuad.getSprite();

        int[] vertices =
                sourceQuad.getVertices().clone();

        final int vertexSize = 8;

        float sourceSpriteMinU =
                sourceSprite.getU0();

        float sourceSpriteMaxU =
                sourceSprite.getU1();

        float sourceSpriteMinV =
                sourceSprite.getV0();

        float sourceSpriteMaxV =
                sourceSprite.getV1();

        float targetSpriteMinU =
                targetSprite.getU0();

        float targetSpriteMaxU =
                targetSprite.getU1();

        float targetSpriteMinV =
                targetSprite.getV0();

        float targetSpriteMaxV =
                targetSprite.getV1();

        float sourceSpriteUSize =
                sourceSpriteMaxU - sourceSpriteMinU;

        float sourceSpriteVSize =
                sourceSpriteMaxV - sourceSpriteMinV;

        float targetSpriteUSize =
                targetSpriteMaxU - targetSpriteMinU;

        float targetSpriteVSize =
                targetSpriteMaxV - targetSpriteMinV;

        if (sourceSpriteUSize == 0.0F
                || sourceSpriteVSize == 0.0F
                || targetSpriteUSize == 0.0F
                || targetSpriteVSize == 0.0F) {

            return sourceQuad;
        }

        for (int vertex = 0; vertex < 4; vertex++) {

            int offset =
                    vertex * vertexSize;

            float u =
                    Float.intBitsToFloat(
                            vertices[offset + 4]
                    );

            float v =
                    Float.intBitsToFloat(
                            vertices[offset + 5]
                    );

            float normalizedU =
                    (u - sourceSpriteMinU)
                            / sourceSpriteUSize;

            float normalizedV =
                    (v - sourceSpriteMinV)
                            / sourceSpriteVSize;

            float newU =
                    targetSpriteMinU
                            + normalizedU
                            * targetSpriteUSize;

            float newV =
                    targetSpriteMinV
                            + normalizedV
                            * targetSpriteVSize;

            vertices[offset + 4] =
                    Float.floatToRawIntBits(newU);

            vertices[offset + 5] =
                    Float.floatToRawIntBits(newV);
        }

        int tintIndex =
                copiedQuad.getTintIndex();

        if (tintIndex >= 0) {
            tintIndex =
                    encodeTintIndex(
                            tintIndex,
                            tintOffset
                    );
        }

        return new BakedQuad(
                vertices,
                tintIndex,
                sourceQuad.getDirection(),
                targetSprite,
                sourceQuad.isShade(),
                sourceQuad.hasAmbientOcclusion()
        );
    }

    /**
     * <h1>RETEXTURING</h1>
     * ================================================================
     * Retextures a material geometry model using the corresponding
     * copied block model.
     */
    public static @NotNull List<BakedQuad> retextureModel(
            @NotNull BakedModel geometryModel,
            @Nullable BlockState copycatState,
            @NotNull BlockState copiedState,
            @Nullable Direction side,
            @NotNull RandomSource random,
            @Nullable RenderType renderType,
            @NotNull ModelData modelData,
            int tintOffset
    ) {
        BakedModel copiedModel =
                BlockModelProvider.getModel(copiedState);

        List<BakedQuad> geometryQuads =
                geometryModel.getQuads(
                        copycatState,
                        side,
                        random,
                        modelData,
                        renderType
                );

        if (geometryQuads.isEmpty()) {
            return List.of();
        }

        List<BakedQuad> result =
                new ArrayList<>();

        for (BakedQuad geometryQuad : geometryQuads) {

            Direction direction =
                    side != null
                            ? side
                            : geometryQuad.getDirection();

            List<BakedQuad> copiedQuads =
                    NeoForgeModelQueries.findMatchingCopiedQuads(
                            copiedModel,
                            copiedState,
                            direction,
                            geometryQuad,
                            random,
                            renderType
                    );

            if (copiedQuads.isEmpty()) {
                result.add(geometryQuad);
                continue;
            }

            for (BakedQuad copiedQuad : copiedQuads) {

                result.add(
                        remapQuad(
                                geometryQuad,
                                geometryQuad.getSprite(),
                                copiedQuad,
                                tintOffset
                        )
                );
            }
        }

        return result;
    }
}
