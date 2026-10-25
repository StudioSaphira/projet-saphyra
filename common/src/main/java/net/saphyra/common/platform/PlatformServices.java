package net.saphyra.common.platform;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Provides access to platform-specific implementations.
 *
 * <p>The active loader installs its implementation before registering mod content.
 * A combined JAR cannot select its platform from the first ServiceLoader entry.</p>
 */
public final class PlatformServices {

    private static volatile PlatformServicesImpl instance;

    public static synchronized void initialize(PlatformServicesImpl implementation) {
        java.util.Objects.requireNonNull(implementation, "implementation");
        if (instance != null) throw new IllegalStateException("Platform services already initialized");
        instance = implementation;
        org.slf4j.LoggerFactory.getLogger(PlatformServices.class)
                .info("SCP Genesis platform services: {}", implementation.getClass().getSimpleName());
    }

    private PlatformServices() {
    }

    /**
     * Returns the platform implementation.
     */
    public static PlatformServicesImpl get() {
        PlatformServicesImpl services = instance;
        if (services == null) throw new IllegalStateException("Platform services have not been initialized by the loader");
        return services;
    }

    /**
     * Returns the platform-specific model data provider.
     */
    public static PlatformModelData<?> modelData() {
        return get().modelData();
    }

    /**
     * Requests a model data update for the specified BlockEntity.
     */
    public static void requestModelDataUpdate(BlockEntity blockEntity) {
        get().requestModelDataUpdate(blockEntity);
    }

    /**
     * Returns the model data associated with the specified BlockEntity.
     */
    public static PlatformModelData.ModelData getModelData(BlockEntity blockEntity) {
        return get().getModelData(blockEntity);
    }
}
