package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryMath;
import net.saphyra.common.api.geometry.GeometryVertex;

/** Transforms custom custom meshes without depending on a block or rendering API. */
public final class GeometryTransforms {
    private GeometryTransforms() {}

    /** Returns clockwise quarter turns from NORTH; rejects vertical directions. */
    public static int rotationCount(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> throw new IllegalArgumentException("custom facing must be horizontal: " + facing);
        };
    }

    /**
     * Copies and orients canonical NORTH/BOTTOM faces. Reflection reverses vertex
     * order to preserve outward winding; neither the input faces nor arrays are modified.
     */
    public static GeometryFace[] orient(GeometryFace[] source, Direction facing, Half half) {
        int rotations = rotationCount(facing);
        GeometryFace[] result = new GeometryFace[source.length];
        for (int i = 0; i < source.length; i++) {
            GeometryVertex[] original = source[i].vertices();
            GeometryVertex[] vertices = new GeometryVertex[original.length];
            for (int j = 0; j < original.length; j++) {
                GeometryVertex vertex = original[j];
                if (half == Half.TOP) vertex = GeometryMath.mirrorY(vertex);
                if (rotations != 0) vertex = GeometryMath.rotateY(vertex, rotations);
                vertices[half == Half.TOP ? original.length - 1 - j : j] = vertex;
            }
            Direction direction = source[i].direction();
            for (int j = 0; j < rotations && direction.getAxis().isHorizontal(); j++) {
                direction = direction.getClockWise();
            }
            if (half == Half.TOP && direction.getAxis() == Direction.Axis.Y) {
                direction = direction.getOpposite();
            }
            result[i] = new GeometryFace(direction, vertices);
        }
        return result;
    }
}
