package net.saphyra.fabric;

import net.fabricmc.api.ModInitializer;
import net.saphyra.common.platform.PlatformServices;
import net.saphyra.fabric.platform.FabricPlatformServices;

@SuppressWarnings("unused")
/** Initializes Saphyra's shared services for the active loader. */
public final class FabricSaphyra implements ModInitializer {

    @Override
    public void onInitialize() {
        PlatformServices.initialize(new FabricPlatformServices());
    }
}
