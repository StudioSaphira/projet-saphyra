package net.saphyra.common.api.geometry;

/**
 * Represents a vertex of a custom mesh geometry.
 *
 * @param x the X coordinate in block-local space
 * @param y the Y coordinate in block-local space
 * @param z the Z coordinate in block-local space
 */
public record GeometryVertex(
        float x,
        float y,
        float z
) {
}
