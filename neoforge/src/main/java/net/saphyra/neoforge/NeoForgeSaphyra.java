package net.saphyra.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.saphyra.common.platform.PlatformServices;
import net.saphyra.neoforge.platform.NeoForgePlatformServices;

@Mod("saphyra")
/** Initializes Saphyra's shared services for the active loader. */
public final class NeoForgeSaphyra {

    public NeoForgeSaphyra(IEventBus modEventBus) {
        PlatformServices.initialize(new NeoForgePlatformServices());
    }
}
