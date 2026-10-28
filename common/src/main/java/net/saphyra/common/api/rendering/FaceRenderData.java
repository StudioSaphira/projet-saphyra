package net.saphyra.common.api.rendering;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryUV;
import org.jetbrains.annotations.Nullable;

/** Client-only face geometry and appearance. UVs are normalized; tint is already resolved by the consumer. */
public record FaceRenderData(GeometryFace face, GeometryUV[] uv, TextureAtlasSprite sprite,
                             @Nullable BakedQuad source, int tintIndex) {

    public FaceRenderData {
        int count = face.vertices().length;
        if ((count != 3 && count != 4) || uv.length != count) {
            throw new IllegalArgumentException("Faces require three or four vertices with matching UVs");
        }
    }
}
