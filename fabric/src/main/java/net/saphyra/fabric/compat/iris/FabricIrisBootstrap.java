package net.saphyra.fabric.compat.iris;

import java.lang.reflect.Field;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.obfuscation.RemapperChain;
import org.slf4j.LoggerFactory;

/** Installs the narrow Iris selector workaround in affected Fabric development launches only. */
public final class FabricIrisBootstrap implements PreLaunchEntrypoint {

    @Override
    @SuppressWarnings("unchecked")
    public void onPreLaunch() {
        var loader = FabricLoader.getInstance();
        if (!loader.isDevelopmentEnvironment()) return;
        var iris = loader.getModContainer("iris");
        if (iris.isEmpty() || !iris.get().getMetadata().getVersion().getFriendlyString()
                .equals("1.8.14-beta.1+mc1.21.1")) return;
        if (!loader.getModContainer("fabricloader").orElseThrow().getMetadata()
                .getVersion().getFriendlyString().equals("0.19.5")) return;
        try {
            // Mixin has no public replace/removal API. Only this verified Loader's
            // development mapper is decorated; neither Iris bytecode nor its JAR is edited.
            Field field = RemapperChain.class.getDeclaredField("remappers");
            field.setAccessible(true);
            List<IRemapper> remappers = (List<IRemapper>) field.get(
                    MixinEnvironment.getDefaultEnvironment().getRemappers());
            int count = 0;
            for (int i = 0; i < remappers.size(); i++) {
                IRemapper mapper = remappers.get(i);
                if (mapper.getClass().getName().equals(
                        "net.fabricmc.loader.impl.util.mappings.MixinIntermediaryDevRemapper")) {
                    remappers.set(i, new IrisDevelopmentRemapper(mapper));
                    count++;
                }
            }
            LoggerFactory.getLogger("Saphyra/Iris").info("Installed {} Iris development selector guard(s)", count);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot install the Iris/Fabric development compatibility guard", failure);
        }
    }
}
