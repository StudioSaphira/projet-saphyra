package net.saphyra.fabric.compat.sodium;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Enables the optional client adapter only for the Sodium ABI verified by Saphyra. */
public final class FabricSodiumMixinPlugin implements IMixinConfigPlugin {

    private boolean enabled;

    @Override
    public void onLoad(String mixinPackage) {
        var mod = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("sodium");
        String version = mod.map(value -> value.getMetadata().getVersion().getFriendlyString()).orElse(null);
        enabled = version != null && (version.equals("0.8.13") || version.startsWith("0.8.13+"));
        var logger = org.slf4j.LoggerFactory.getLogger("Saphyra/Sodium");
        if (enabled) logger.info("Sodium {} detected: enabling optional RGB and dynamic-light adapter (Fabric)", version);
        else if (version != null) logger.warn("Sodium {} has not been validated by this RGB adapter (expected 0.8.13); adapter disabled", version);
    }

    @Override
    public boolean shouldApplyMixin(String target, String mixin) {
        return enabled;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override public void acceptTargets(Set<String> mine, Set<String> others) {}
    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override public void preApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
    @Override public void postApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
}
