package net.saphyra.neoforge.client.lighting;

import net.saphyra.common.client.lighting.LightingRuntime;
import net.saphyra.common.platform.LightingPlatform;
import net.saphyra.common.platform.PlatformServices;
import net.saphyra.neoforge.platform.NeoForgeLightingPlatform;

/** Installs the client lighting platform and its tick callback once. */
public final class NeoForgeLighting {

    private NeoForgeLighting() {}

    public static void initialize() {
        PlatformServices.registerExtension(LightingPlatform.class, new NeoForgeLightingPlatform());
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.client.event.ClientTickEvent.Post event) -> LightingRuntime.tick());
    }
}
