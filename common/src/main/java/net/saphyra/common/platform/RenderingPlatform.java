package net.saphyra.common.platform;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.api.rendering.FaceRenderData;
import net.saphyra.common.api.rendering.FaceRenderTarget;

/** Client-only rendering contract. Never resolve this service from a dedicated server. */
public interface RenderingPlatform {

    /** Converts one generic face into the active renderer's native representation. */
    void renderFace(FaceRenderData data, FaceRenderTarget target);

    /** Bakes a base model with the loader's sprite-aware baking API. */
    BakedModel bakeModel(ModelBaker baker, ResourceLocation id, ModelState state,
                         Function<Material, TextureAtlasSprite> sprites);
}
