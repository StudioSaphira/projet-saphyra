package net.saphyra.neoforge.platform;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.api.rendering.*;
import net.saphyra.common.platform.RenderingPlatform;
import net.saphyra.neoforge.client.rendering.*;

/** Translates Saphyra's rendering contract to the NeoForge client API. */
public final class NeoForgeRenderingPlatform implements RenderingPlatform {

    @Override
    public void renderFace(FaceRenderData data, FaceRenderTarget target) {
        if (!(target instanceof BakedQuadTarget destination)) {
            throw new IllegalArgumentException("NeoForge rendering requires a BakedQuadTarget");
        }
        destination.output().accept(NeoForgeFaceBaker.bake(data.face(), data.uv(), data.sprite(),
                data.source(), data.tintIndex()));
    }

    @Override
    public BakedModel bakeModel(ModelBaker baker, ResourceLocation id, ModelState state,
                                Function<Material, TextureAtlasSprite> sprites) {
        return NeoForgeModelBaking.bakeModel(baker, id, state, sprites);
    }
}
