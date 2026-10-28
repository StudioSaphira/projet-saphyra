package net.saphyra.neoforge.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryVertex;
import net.saphyra.common.api.geometry.OutlineGeometry;
import net.saphyra.common.api.rendering.OutlineDrawing;

/** Adapts NeoForge baked model data to the shared custom outline renderer. */
public final class NeoForgeOutlineDrawing {
    private NeoForgeOutlineDrawing() {}

    /** Draws all baked face edges, including unculled faces and degenerate triangle quads. */
    public static void render(PoseStack stack, MultiBufferSource buffers, BlockPos pos,
                              BlockState state, ModelData data) {
        Minecraft minecraft = Minecraft.getInstance();
        var camera = minecraft.gameRenderer.getMainCamera().getPosition();
        BakedModel model = minecraft.getBlockRenderer().getBlockModelShaper().getBlockModel(state);
        var quads = collectQuads(model, state, data);
        GeometryFace[] faces = new GeometryFace[quads.size()];
        for (int i = 0; i < faces.length; i++) {
            BakedQuad quad = quads.get(i);
            int[] packed = quad.getVertices();
            GeometryVertex[] vertices = new GeometryVertex[4];
            for (int j = 0; j < 4; j++) {
                int offset = j * 8;
                vertices[j] = new GeometryVertex(Float.intBitsToFloat(packed[offset]),
                        Float.intBitsToFloat(packed[offset + 1]), Float.intBitsToFloat(packed[offset + 2]));
            }
            faces[i] = new GeometryFace(quad.getDirection(), vertices);
        }
        var lines = OutlineGeometry.lines(faces);
        if (lines.isEmpty()) return;
        var buffer = buffers.getBuffer(RenderType.lines());
        stack.pushPose();
        try {
            stack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            OutlineDrawing.draw(stack.last(), buffer, lines, 0, 0, 0);
        } finally {
            stack.popPose();
        }
    }

    private static List<BakedQuad> collectQuads(BakedModel model, BlockState state, ModelData data) {
        List<BakedQuad> result = new ArrayList<>();
        RandomSource random = RandomSource.create(42L);
        for (Direction side : Direction.values()) result.addAll(model.getQuads(state, side, random, data, null));
        result.addAll(model.getQuads(state, null, random, data, null));
        return result;
    }
}
