package net.saphyra.neoforge.client.rendering;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryMath;
import net.saphyra.common.api.geometry.GeometryUV;
import net.saphyra.common.api.geometry.GeometryVector;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.Nullable;

/** Packs custom triangular or quadrilateral faces into NeoForge's baked vertex format. */
public final class NeoForgeFaceBaker {
    private static final int STRIDE = 8;
    private static final int COLOR = 3;
    private static final int UV = 4;
    private static final int LIGHT = 6;
    private static final int NORMAL = 7;
    private NeoForgeFaceBaker() {}

    /**
     * Bakes normalized positions and UVs, repeating the last vertex for triangles.
     * A source quad supplies per-vertex color/light, tint, shade and AO flags;
     * null selects white, unlit vertex data and the default shade/AO flags.
     */
    public static BakedQuad bake(GeometryFace face, GeometryUV[] uv, TextureAtlasSprite sprite,
                                 @Nullable BakedQuad source, int tintIndex) {
        int count = face.vertices().length;
        if ((count != 3 && count != 4) || uv.length != count) {
            throw new IllegalArgumentException("custom faces require 3 or 4 vertices with matching UVs");
        }
        int[] vertices = new int[4 * STRIDE];
        GeometryVector normal = GeometryMath.normalize(GeometryMath.normal(face));
        int packedNormal = pack(normal.x()) | (pack(normal.y()) << 8) | (pack(normal.z()) << 16);
        for (int i = 0; i < 4; i++) {
            int index = Math.min(i, count - 1);
            int offset = i * STRIDE;
            GeometryVertex vertex = face.vertices()[index];
            vertices[offset] = Float.floatToRawIntBits(vertex.x());
            vertices[offset + 1] = Float.floatToRawIntBits(vertex.y());
            vertices[offset + 2] = Float.floatToRawIntBits(vertex.z());
            vertices[offset + COLOR] = source == null ? -1 : source.getVertices()[offset + COLOR];
            vertices[offset + UV] = Float.floatToRawIntBits(sprite.getU(uv[index].u()));
            vertices[offset + UV + 1] = Float.floatToRawIntBits(sprite.getV(uv[index].v()));
            vertices[offset + LIGHT] = source == null ? 0 : source.getVertices()[offset + LIGHT];
            vertices[offset + NORMAL] = packedNormal;
        }
        return new BakedQuad(vertices, tintIndex, face.direction(), sprite,
                source == null || source.isShade(), source == null || source.hasAmbientOcclusion());
    }

    private static int pack(float component) {
        return Math.round(Math.clamp(component, -1.0F, 1.0F) * 127.0F) & 0xFF;
    }
}
