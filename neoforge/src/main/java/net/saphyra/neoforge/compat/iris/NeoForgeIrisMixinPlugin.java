package net.saphyra.neoforge.compat.iris;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Enables the optional client adapter only for the Iris ABI verified by Saphyra. */
public final class NeoForgeIrisMixinPlugin implements IMixinConfigPlugin {

    private boolean enabled;

    @Override
    public void onLoad(String mixinPackage) {
        var mods = net.neoforged.fml.loading.LoadingModList.get();
        String version = mods == null ? null : mods.getMods().stream().filter(mod -> mod.getModId().equals("iris"))
                .map(mod -> mod.getVersion().toString()).findFirst().orElse(null);
        enabled = version != null && (version.equals("1.8.14-beta.1") || version.startsWith("1.8.14-beta.1+"));
        var logger = org.slf4j.LoggerFactory.getLogger("Saphyra/Iris");
        if (enabled) logger.info("Iris {} detected: enabling optional RGB and dynamic-light adapter (NeoForge)", version);
        else if (version != null) logger.warn("Iris {} has not been validated by this RGB adapter (expected 1.8.14-beta.1); adapter disabled", version);
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
