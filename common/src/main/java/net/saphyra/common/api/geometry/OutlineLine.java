package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;

/**
 * One outline edge in normalized block coordinates, shared by both rendering platforms.
 * @param start first endpoint
 * @param end second endpoint
 * @param direction nominal face direction used by the line shader
 */
public record OutlineLine(GeometryVertex start, GeometryVertex end, Direction direction) {
    /** Returns the nominal face normal, preserving the existing outline lighting. */
    public GeometryVector normal() {
        return new GeometryVector(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }
}
