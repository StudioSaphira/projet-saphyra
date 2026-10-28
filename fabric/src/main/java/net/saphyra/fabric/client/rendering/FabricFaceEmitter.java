package net.saphyra.fabric.client.rendering;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryMath;
import net.saphyra.common.api.geometry.GeometryUV;
import net.saphyra.common.api.geometry.GeometryVector;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.Nullable;

/** Emits triangular or quadrilateral custom faces; callers supply their own culling policy. */
public final class FabricFaceEmitter {
    private FabricFaceEmitter() {}

    /** Copies material appearance, then replaces geometry and normalized UVs. */
    public static void retexture(QuadEmitter emitter, GeometryFace face, GeometryUV[] uv,
                                 BakedQuad source, TextureAtlasSprite sprite, int tintIndex, RenderMaterial material,
                                 @Nullable Direction cullFace) {
        emitter.fromVanilla(source, material, cullFace);
        geometry(emitter, face, uv);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.nominalFace(cullFace);
        emitter.colorIndex(tintIndex);
        emitter.emit();
    }

    /** Emits a face without a source quad, retaining the default mesh's unculled behavior. */
    public static void empty(QuadEmitter emitter, GeometryFace face, GeometryUV[] uv,
                             TextureAtlasSprite sprite, int tintIndex, RenderMaterial material,
                             @Nullable Direction nominalFace) {
        geometry(emitter, face, uv);
        emitter.color(-1, -1, -1, -1);
        emitter.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
        emitter.material(material);
        emitter.nominalFace(nominalFace);
        emitter.colorIndex(tintIndex);
        emitter.emit();
    }

    /** A triangle repeats its last vertex because Fabric always consumes four vertices. */
    private static void geometry(QuadEmitter emitter, GeometryFace face, GeometryUV[] uv) {
        int count = face.vertices().length;
        if ((count != 3 && count != 4) || uv.length != count) {
            throw new IllegalArgumentException("custom faces require 3 or 4 vertices with matching UVs");
        }
        GeometryVector normal = GeometryMath.normalize(GeometryMath.normal(face));
        for (int i = 0; i < 4; i++) {
            int index = Math.min(i, count - 1);
            GeometryVertex vertex = face.vertices()[index];
            emitter.pos(i, vertex.x(), vertex.y(), vertex.z());
            emitter.normal(i, normal.x(), normal.y(), normal.z());
            emitter.uv(i, uv[index].u(), uv[index].v());
        }
    }
}
