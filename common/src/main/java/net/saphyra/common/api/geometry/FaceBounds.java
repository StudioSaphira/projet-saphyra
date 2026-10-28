package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.Nullable;

/** Geometric boundary queries for custom custom faces, independent of a renderer's culling policy. */
public final class FaceBounds {
    private static final float EPSILON = 1.0e-5F;
    private FaceBounds() {}

    /**
     * Returns the nominal boundary containing every vertex, or null for an interior/sloped face.
     * A triangle may belong to a boundary without covering it completely.
     */
    public static @Nullable Direction boundaryDirection(GeometryFace face) {
        if (face.vertices().length == 0) return null;
        Direction direction = face.direction();
        float boundary = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0;
        for (GeometryVertex vertex : face.vertices()) {
            if (Math.abs(coordinate(vertex, direction.getAxis()) - boundary) > EPSILON) return null;
        }
        return direction;
    }

    /** Requires a boundary quad containing all four corners, not merely a full-size bounding box. */
    public static boolean isFullBoundary(GeometryFace face) {
        if (face.vertices().length != 4 || boundaryDirection(face) == null) return false;
        Direction.Axis normal = face.direction().getAxis();
        Direction.Axis first = normal == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X;
        Direction.Axis second = normal == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z;
        for (int a = 0; a <= 1; a++) {
            for (int b = 0; b <= 1; b++) {
                boolean found = false;
                for (GeometryVertex vertex : face.vertices()) {
                    if (Math.abs(coordinate(vertex, first) - a) <= EPSILON
                            && Math.abs(coordinate(vertex, second) - b) <= EPSILON) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return true;
    }

    private static float coordinate(GeometryVertex vertex, Direction.Axis axis) {
        return switch (axis) {
            case X -> vertex.x();
            case Y -> vertex.y();
            case Z -> vertex.z();
        };
    }
}
