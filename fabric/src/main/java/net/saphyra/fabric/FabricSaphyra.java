package net.saphyra.fabric;

import net.fabricmc.api.ModInitializer;

import net.saphyra.common.platform.PlatformServices;
import net.saphyra.fabric.platform.FabricPlatformRegistry;
import net.saphyra.fabric.platform.FabricPlatformServices;

@SuppressWarnings("unused")
public final class FabricSaphyra implements ModInitializer {

    @Override
    public void onInitialize() {
        PlatformServices.initialize(
                new FabricPlatformServices());
        FabricPlatformRegistry registry = new FabricPlatformRegistry();
    }
}
