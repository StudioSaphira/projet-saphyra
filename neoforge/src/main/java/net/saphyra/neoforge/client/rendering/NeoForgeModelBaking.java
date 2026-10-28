package net.saphyra.neoforge.client.rendering;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

/** Reusable client rendering utility, independent of consuming mods. */
public final class NeoForgeModelBaking {

    public static BakedModel bakeModel(
            ModelBaker baker,
            ResourceLocation modelLocation,
            ModelState modelState,
            Function<Material, TextureAtlasSprite> spriteGetter
    ) {
        if (modelLocation == null) {
            throw new IllegalStateException(
                    "Missing custom base model"
            );
        }

        BakedModel model = baker.bake(
                modelLocation,
                modelState,
                spriteGetter
        );

        if (model == null) {
            throw new IllegalStateException(
                    "Failed to bake custom base model: "
                            + modelLocation
            );
        }

        return model;
    }
}
