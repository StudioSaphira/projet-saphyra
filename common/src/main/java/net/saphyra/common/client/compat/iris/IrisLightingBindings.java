package net.saphyra.common.client.compat.iris;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.IntSupplier;
import net.saphyra.common.client.lighting.LightingRuntime;

/** Optional Iris ABI access, loaded only by the version-gated loader adapters. */
public final class IrisLightingBindings {

    private IrisLightingBindings() {}

    /** Reads the configured pack even while Iris is constructing its source graph. */
    public static String packName() {
        try {
            Object config = Class.forName("net.irisshaders.iris.Iris").getMethod("getIrisConfig").invoke(null);
            return ((Optional<?>) config.getClass().getMethod("getShaderPackName").invoke(config))
                    .map(Object::toString).orElse("");
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Unsupported Iris configuration ABI", failure);
        }
    }

    /** Lets Iris allocate free sampler units; no fixed texture slot is overwritten. */
    public static void register(Object builder) {
        try {
            Class<?> type = Class.forName("net.irisshaders.iris.gl.texture.TextureType");
            Object texture2d = type.getField("TEXTURE_2D").get(null);
            Class<?> sampler = Class.forName("net.irisshaders.iris.gl.sampler.GlSampler");
            Method has = builder.getClass().getMethod("hasSampler", String.class);
            Method add = builder.getClass().getMethod("addDynamicSampler", type,
                    IntSupplier.class, sampler, String[].class);
            for (String name : new String[]{"SaphyraLightVolume", "SaphyraStaticAtlas", "SaphyraStaticPages"}) {
                if (!(boolean) has.invoke(builder, name)) continue;
                IntSupplier texture = () -> LightingRuntime.lightingTexture(name);
                if (!(boolean) add.invoke(builder, texture2d, texture, null, new String[]{name})) {
                    throw new IllegalStateException("Iris could not allocate light sampler: " + name);
                }
            }
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Unsupported Iris sampler ABI", failure);
        }
    }
}
