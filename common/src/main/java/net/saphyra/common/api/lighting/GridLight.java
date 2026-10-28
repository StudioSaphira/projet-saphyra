package net.saphyra.common.api.lighting;

/** One immutable source in local voxel coordinates; custom also marks mobile white sources. */
public record GridLight(int x, int y, int z, LightEmission emission, boolean custom) {}
