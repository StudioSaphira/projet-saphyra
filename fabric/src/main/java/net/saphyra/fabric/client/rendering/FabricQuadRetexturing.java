package net.saphyra.fabric.client.rendering;

import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.rendering.BlockModelProvider;
import net.saphyra.fabric.client.rendering.FabricModelQueries;
import net.saphyra.fabric.client.rendering.FabricRenderMaterials;
import org.jetbrains.annotations.NotNull;

/** Retextures supplied models without knowing the consumer block or material slots. */
public final class FabricQuadRetexturing {

    private FabricQuadRetexturing() {
    }

    /**
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
     * Emits a vanilla BakedQuad through the Fabric Renderer API.
     */
    public static void emitQuad(
            @NotNull QuadEmitter emitter,
            @NotNull BakedQuad quad,
            @NotNull RenderMaterial material
    ) {
        emitter.fromVanilla(
                quad,
                material,
                null
        );

        emitter.emit();
    }

    /**
     * Emits a geometry quad retextured using the sprite of a copied quad.
     *
     * <p>This is the Fabric equivalent of NeoForge's
     * {@code remapQuad()}.</p>
     *
     * <p>The geometry itself always comes from {@code geometryQuad}.
     * Only its UV coordinates are remapped from the geometry sprite
     * to the copied sprite.</p>
     */
    public static void emitRetexturedQuad(
            @NotNull QuadEmitter emitter,
            @NotNull BakedQuad geometryQuad,
            @NotNull BakedQuad copiedQuad,
            int tintOffset
    ) {
        emitter.fromVanilla(
                geometryQuad,
                FabricRenderMaterials.getCutoutMaterial(),
                geometryQuad.getDirection()
        );

        emitter.spriteBake(
                copiedQuad.getSprite(),
                MutableQuadView.BAKE_LOCK_UV
        );

        emitter.colorIndex(
                encodeTintIndex(
                        copiedQuad.getTintIndex(),
                        tintOffset
                )
        );

        emitter.emit();
    }

    private static List<BakedQuad> getAllGeometryQuads(
            @NotNull BakedModel model,
            @NotNull BlockState state,
            @NotNull RandomSource random
    ) {

        List<BakedQuad> result = new java.util.ArrayList<>(model.getQuads(
                state,
                null,
                random
        ));

        for (Direction direction : Direction.values()) {
            result.addAll(
                    model.getQuads(
                            state,
                            direction,
                            random
                    )
            );
        }

        return result;
    }

    public static void emitBaseModel(
            @NotNull BakedModel model,
            @NotNull BlockState state,
            @NotNull Supplier<RandomSource> randomSupplier,
            @NotNull RenderContext context
    ) {
        RandomSource random = randomSupplier.get();

        QuadEmitter emitter = context.getEmitter();

        RenderMaterial material =
                FabricRenderMaterials.getCutoutMaterial();

        List<BakedQuad> quads =
                getAllGeometryQuads(
                        model,
                        state,
                        random
                );

        for (BakedQuad quad : quads) {
            emitQuad(
                    emitter,
                    quad,
                    material
            );
        }
    }

    /**
     * Emits a retextured material model through Fabric Renderer.
     */
    public static void retextureModel(
            @NotNull BakedModel geometryModel,
            @SuppressWarnings("unused") @NotNull BlockAndTintGetter blockView,
            @NotNull BlockState copycatState,
            @SuppressWarnings("unused") @NotNull BlockPos pos,
            @NotNull BlockState copiedState,
            @NotNull Supplier<RandomSource> randomSupplier,
            @NotNull RenderContext context,
            int tintOffset
    ) {
        BakedModel copiedModel =
                BlockModelProvider.getModel(copiedState);

        QuadEmitter emitter =
                context.getEmitter();

        RandomSource random =
                randomSupplier.get();

        List<BakedQuad> geometryQuads =
                getAllGeometryQuads(
                        geometryModel,
                        copycatState,
                        random
                );

        if (geometryQuads.isEmpty()) {
            return;
        }

        for (BakedQuad geometryQuad : geometryQuads) {

            Direction direction =
                    geometryQuad.getDirection();

            List<BakedQuad> copiedQuads =
                    FabricModelQueries.findMatchingCopiedQuads(
                            copiedModel,
                            copiedState,
                            direction,
                            geometryQuad,
                            random
                    );

            if (copiedQuads.isEmpty()) {
                emitQuad(
                        emitter,
                        geometryQuad,
                        FabricRenderMaterials.getCutoutMaterial()
                );

                continue;
            }

            for (BakedQuad copiedQuad : copiedQuads) {
                emitRetexturedQuad(
                        emitter,
                        geometryQuad,
                        copiedQuad,
                        tintOffset
                );
            }
        }
    }
}
