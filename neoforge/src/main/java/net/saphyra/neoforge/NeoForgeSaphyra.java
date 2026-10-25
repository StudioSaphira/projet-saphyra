package net.saphyra.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.saphyra.common.platform.PlatformServices;

import net.saphyra.neoforge.platform.NeoForgePlatformServices;
import net.saphyra.neoforge.platform.NeoForgePlatformRegistry;

@Mod("saphyra")
public final class NeoForgeSaphyra {

    public NeoForgeSaphyra(IEventBus modEventBus) {
        PlatformServices.initialize(
                new NeoForgePlatformServices());
        NeoForgePlatformRegistry registry = new NeoForgePlatformRegistry(modEventBus);

        registry.register();
    }
}
