package net.saphyra.fabric.client.rendering;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

/** Reusable client rendering utility, independent of consuming mods. */
public final class FabricRenderMaterials {

    private FabricRenderMaterials() {
    }

    /**
     * Returns the active Fabric Renderer.
     */
    public static Renderer getRenderer() {
        Renderer renderer =
                RendererAccess.INSTANCE.getRenderer();

        if (renderer == null) {
            throw new IllegalStateException("Fabric Renderer is not available");
        }

        return renderer;
    }

    /**
     * Returns the standard Fabric render material.
     */
    public static RenderMaterial getCutoutMaterial() {
        return getRenderer()
                .materialFinder()
                .clear()
                .blendMode(BlendMode.CUTOUT)
                .find();
    }
}
