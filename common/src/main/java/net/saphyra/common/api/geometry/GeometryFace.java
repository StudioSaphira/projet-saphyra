package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a face of a custom mesh geometry.
 *
 * @param direction the nominal direction of the face
 * @param vertices the vertices composing the face
 */
public record GeometryFace(
        @NotNull Direction direction,
        @NotNull GeometryVertex[] vertices
) {
}
