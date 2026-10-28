package net.saphyra.common.client.lighting;

import net.minecraft.core.BlockPos;

/** Completed light field, associated with the world generation that requested it. */
public record LightingResult(int generation, BlockPos origin, byte[] levels) {}
