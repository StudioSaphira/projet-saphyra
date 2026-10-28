package net.saphyra.common.api.rendering;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.platform.PlatformServices;
import net.saphyra.common.platform.RenderingPlatform;

/** Client facade selecting the renderer installed by Saphyra's active loader. */
public final class SaphyraRendering {

    private SaphyraRendering() {}

    public static void renderFace(FaceRenderData data, FaceRenderTarget target) {
        PlatformServices.extension(RenderingPlatform.class).renderFace(data, target);
    }

    public static BakedModel bakeModel(ModelBaker baker, ResourceLocation id, ModelState state,
                                       Function<Material, TextureAtlasSprite> sprites) {
        return PlatformServices.extension(RenderingPlatform.class).bakeModel(baker, id, state, sprites);
    }
}
