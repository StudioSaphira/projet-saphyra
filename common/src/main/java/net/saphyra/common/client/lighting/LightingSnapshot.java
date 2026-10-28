package net.saphyra.common.client.lighting;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.saphyra.common.api.lighting.GridLight;

/** World-independent snapshot owned by a single pending propagation job. */
public record LightingSnapshot(BlockPos origin, byte[] opacity, List<GridLight> sources) {}
