package net.saphyra.common.api.rendering;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.geometry.GeometryFace;
import org.jetbrains.annotations.Nullable;

/** Client-only provider registry. Consumers register their identifiers and geometry, never loader events. */
public final class OutlineProviders {

    private static final Map<ResourceLocation, OutlineProvider> PROVIDERS = new LinkedHashMap<>();

    private OutlineProviders() {}

    public static synchronized void register(ResourceLocation id, OutlineProvider provider) {
        if (PROVIDERS.putIfAbsent(id, provider) != null) {
            throw new IllegalStateException("Outline provider already registered: " + id);
        }
    }

    public static synchronized @Nullable GeometryFace[] faces(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        for (OutlineProvider provider : PROVIDERS.values()) {
            GeometryFace[] faces = provider.faces(level, pos, state);
            if (faces != null) return faces;
        }
        return null;
    }
}
