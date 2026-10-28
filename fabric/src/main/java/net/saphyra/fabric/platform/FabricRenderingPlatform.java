package net.saphyra.fabric.platform;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.api.rendering.*;
import net.saphyra.common.platform.RenderingPlatform;
import net.saphyra.fabric.client.rendering.*;

/** Translates Saphyra's rendering contract to the Fabric client API. */
public final class FabricRenderingPlatform implements RenderingPlatform {

    @Override
    public void renderFace(FaceRenderData data, FaceRenderTarget target) {
        if (!(target instanceof FabricFaceTarget destination)) {
            throw new IllegalArgumentException("Fabric rendering requires a FabricFaceTarget");
        }
        if (data.source() == null) {
            FabricFaceEmitter.empty(destination.emitter(), data.face(), data.uv(), data.sprite(), data.tintIndex(),
                    destination.material(), destination.nominalFace());
        } else {
            FabricFaceEmitter.retexture(destination.emitter(), data.face(), data.uv(), data.source(), data.sprite(),
                    data.tintIndex(), destination.material(), destination.nominalFace());
        }
    }

    @Override
    public BakedModel bakeModel(ModelBaker baker, ResourceLocation id, ModelState state,
                                Function<Material, TextureAtlasSprite> sprites) {
        return FabricModelBaking.bakeModel(baker, id, state, sprites);
    }
}
