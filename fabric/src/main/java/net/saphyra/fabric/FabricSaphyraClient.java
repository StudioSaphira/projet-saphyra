package net.saphyra.fabric;

import net.fabricmc.api.ClientModInitializer;

/** Client entry point installing the shared rendering backend. */
@SuppressWarnings("unused")
public final class FabricSaphyraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        net.saphyra.common.platform.PlatformServices.registerExtension(
                net.saphyra.common.platform.RenderingPlatform.class,
                new net.saphyra.fabric.platform.FabricRenderingPlatform());
        net.saphyra.fabric.client.rendering.FabricOutlineEvents.register();
        net.saphyra.fabric.client.lighting.FabricLighting.initialize();
    }
}
