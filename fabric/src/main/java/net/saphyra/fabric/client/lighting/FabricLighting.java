package net.saphyra.fabric.client.lighting;

import net.saphyra.common.client.lighting.LightingRuntime;
import net.saphyra.common.platform.LightingPlatform;
import net.saphyra.common.platform.PlatformServices;
import net.saphyra.fabric.platform.FabricLightingPlatform;

/** Installs the client lighting platform and its tick callback once. */
public final class FabricLighting {

    private FabricLighting() {}

    public static void initialize() {
        PlatformServices.registerExtension(LightingPlatform.class, new FabricLightingPlatform());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> LightingRuntime.tick());
    }
}
