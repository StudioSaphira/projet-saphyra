package net.saphyra.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = "saphyra", dist = Dist.CLIENT)
/** Client entry point installing the shared rendering backend. */
public final class NeoForgeSaphyraClient {

    public NeoForgeSaphyraClient() {
        net.saphyra.common.platform.PlatformServices.registerExtension(
                net.saphyra.common.platform.RenderingPlatform.class,
                new net.saphyra.neoforge.platform.NeoForgeRenderingPlatform());
        net.saphyra.neoforge.client.rendering.NeoForgeOutlineEvents.register();
        net.saphyra.neoforge.client.lighting.NeoForgeLighting.initialize();
    }
}
