package net.saphyra.common.platform;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Access to the shared backend installed by Saphyra's active loader.
 *
 * <p>Mods must not replace this backend. They may register their own service interfaces as
 * extensions; an extension is identified by its interface class, so unrelated mods remain isolated.
 */
public final class PlatformServices {

    private static volatile PlatformServicesImpl instance;
    private static final Map<Class<?>, Object> EXTENSIONS = new HashMap<>();

    private PlatformServices() {}

    /** Installs the loader backend once, from Saphyra's entry point. */
    public static synchronized void initialize(PlatformServicesImpl implementation) {
        Objects.requireNonNull(implementation, "implementation");
        if (instance != null) {
            throw new IllegalStateException("Saphyra platform services already initialized");
        }
        instance = implementation;
    }

    /** Returns the backend after Saphyra initialization. */
    public static PlatformServicesImpl get() {
        PlatformServicesImpl services = instance;
        if (services == null) {
            throw new IllegalStateException(
                    "Saphyra has not initialized its platform services yet");
        }
        return services;
    }

    /** Requests a client-side model-data refresh without exposing a loader API to Common. */
    public static void requestModelDataUpdate(BlockEntity blockEntity) {
        get().requestModelDataUpdate(blockEntity);
    }

    /** Installs one mod-owned implementation without replacing shared services. */
    public static synchronized <T> void registerExtension(Class<T> contract, T implementation) {
        Objects.requireNonNull(contract, "contract");
        Objects.requireNonNull(implementation, "implementation");
        if (!contract.isInterface()) {
            throw new IllegalArgumentException("An extension contract must be an interface");
        }
        T checked = contract.cast(implementation);
        if (EXTENSIONS.putIfAbsent(contract, checked) != null) {
            throw new IllegalStateException("Extension already registered: " + contract.getName());
        }
    }

    /** Looks up a mod extension; call only after that mod's loader initialization. */
    public static synchronized <T> T extension(Class<T> contract) {
        Object implementation = EXTENSIONS.get(Objects.requireNonNull(contract, "contract"));
        if (implementation == null) {
            throw new IllegalStateException("Missing platform extension: " + contract.getName());
        }
        return contract.cast(implementation);
    }
}
