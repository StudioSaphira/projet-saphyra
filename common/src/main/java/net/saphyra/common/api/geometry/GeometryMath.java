package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryVector;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.NotNull;

/**
 * Provides mathematical operations used by custom mesh geometry.
 *
 * <p>This class contains only platform-independent geometry
 * calculations. It does not depend on Fabric, NeoForge, or any
 * rendering API.</p>
 *
 * <p>All positions use normalized block-local coordinates in the
 * range {@code 0.0F} to {@code 1.0F}.</p>
 */
public final class GeometryMath {

    private GeometryMath() {}

    public static @NotNull GeometryVertex lerp(
            @NotNull GeometryVertex a,
            @NotNull GeometryVertex b,
            float t
    ) {
        return new GeometryVertex(
                a.x() + (b.x() - a.x()) * t,
                a.y() + (b.y() - a.y()) * t,
                a.z() + (b.z() - a.z()) * t
        );
    }

    /**
     * Subtracts one vertex position from another and returns the
     * resulting displacement vector.
     *
     * <p>The operation performed is:</p>
     *
     * <pre>
     * result = b - a
     * </pre>
     *
     * @param a the origin position
     * @param b the target position
     * @return the vector from {@code a} to {@code b}
     */
    public static @NotNull GeometryVector subtract(
            @NotNull GeometryVertex a,
            @NotNull GeometryVertex b
    ) {
        return new GeometryVector(
                b.x() - a.x(),
                b.y() - a.y(),
                b.z() - a.z()
        );
    }

    /**
     * Adds two vectors component by component.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the resulting vector
     */
    public static @NotNull GeometryVector add(
            @NotNull GeometryVector a,
            @NotNull GeometryVector b
    ) {
        return new GeometryVector(
                a.x() + b.x(),
                a.y() + b.y(),
                a.z() + b.z()
        );
    }

    /**
     * Offsets a vertex position by the specified vector.
     *
     * <p>The operation performed is:</p>
     *
     * <pre>
     * result = vertex + offset
     * </pre>
     *
     * @param vertex the original vertex position
     * @param offset the displacement vector
     * @return the translated vertex
     */
    public static @NotNull GeometryVertex offset(
            @NotNull GeometryVertex vertex,
            @NotNull GeometryVector offset
    ) {
        return new GeometryVertex(
                vertex.x() + offset.x(),
                vertex.y() + offset.y(),
                vertex.z() + offset.z()
        );
    }

    /**
     * Rotates a vertex around the vertical Y axis and the center
     * of the block.
     *
     * <p>Each rotation represents a clockwise 90-degree rotation
     * when viewed from above.</p>
     *
     * <ul>
     *     <li>{@code 0} = NORTH</li>
     *     <li>{@code 1} = EAST</li>
     *     <li>{@code 2} = SOUTH</li>
     *     <li>{@code 3} = WEST</li>
     * </ul>
     *
     * <p>The rotation is performed around the center point
     * {@code (0.5, 0.5, 0.5)}. The Y coordinate remains unchanged.</p>
     *
     * @param vertex the vertex to rotate
     * @param rotations the number of clockwise 90 degree rotations
     * @return the rotated vertex
     */
    public static @NotNull GeometryVertex rotateY(
            @NotNull GeometryVertex vertex,
            int rotations
    ) {
        int normalizedRotations =
                Math.floorMod(
                        rotations,
                        4
                );

        if (normalizedRotations == 0) {
            return vertex;
        }

        float localX =
                vertex.x() - 0.5F;

        float localZ =
                vertex.z() - 0.5F;

        float rotatedX =
                localX;

        float rotatedZ =
                localZ;

        for (int i = 0; i < normalizedRotations; i++) {

            float previousX =
                    rotatedX;

            rotatedX =
                    -rotatedZ;

            rotatedZ =
                    previousX;
        }

        return new GeometryVertex(
                rotatedX + 0.5F,
                vertex.y(),
                rotatedZ + 0.5F
        );
    }

    /**
     * Mirrors a vertex vertically around the horizontal plane
     * located at {@code Y = 0.5}.
     *
     * <p>The operation performed is:</p>
     *
     * <pre>
     * Y' = 1 - Y
     * </pre>
     *
     * <p>X and Z remain unchanged.</p>
     *
     * @param vertex the vertex to mirror
     * @return the vertically mirrored vertex
     */
    public static @NotNull GeometryVertex mirrorY(
            @NotNull GeometryVertex vertex
    ) {
        return new GeometryVertex(
                vertex.x(),
                1.0F - vertex.y(),
                vertex.z()
        );
    }

    /**
     * Calculates the cross product of two vectors.
     *
     * <p>The resulting vector is perpendicular to both input
     * vectors and can therefore be used to determine a face normal.</p>
     *
     * <pre>
     * a × b =
     *
     * ( a.y * b.z - a.z * b.y,
     *   a.z * b.x - a.x * b.z,
     *   a.x * b.y - a.y * b.x )
     * </pre>
     *
     * @param a the first vector
     * @param b the second vector
     * @return the cross product
     */
    public static @NotNull GeometryVector cross(
            @NotNull GeometryVector a,
            @NotNull GeometryVector b
    ) {
        return new GeometryVector(
                a.y() * b.z()
                        - a.z() * b.y(),

                a.z() * b.x()
                        - a.x() * b.z(),

                a.x() * b.y()
                        - a.y() * b.x()
        );
    }

    /**
     * Calculates the geometric normal of a polygon face.
     *
     * <p>The normal is calculated from the first three vertices:</p>
     *
     * <pre>
     * A = vertex 0
     * B = vertex 1
     * C = vertex 2
     *
     * AB = B - A
     * AC = C - A
     *
     * normal = AB × AC
     * </pre>
     *
     * <p>The direction of the resulting normal depends on the
     * winding order of the face vertices.</p>
     *
     * @param face the face whose normal should be calculated
     * @return the unnormalized geometric normal
     * @throws IllegalArgumentException if the face contains fewer
     * than three vertices
     */
    public static @NotNull GeometryVector normal(
            @NotNull GeometryFace face
    ) {
        GeometryVertex[] vertices =
                face.vertices();

        if (vertices.length < 3) {
            throw new IllegalArgumentException(
                    "A polygon face must contain at least three vertices"
            );
        }

        GeometryVertex a =
                vertices[0];

        GeometryVertex b =
                vertices[1];

        GeometryVertex c =
                vertices[2];

        GeometryVector ab =
                subtract(
                        a,
                        b
                );

        GeometryVector ac =
                subtract(
                        a,
                        c
                );

        return cross(
                ab,
                ac
        );
    }

    /**
     * Returns the squared length of a vector.
     *
     * <p>This avoids the square-root operation and is useful when
     * only relative vector lengths are required.</p>
     *
     * @param vector the vector
     * @return the squared length
     */
    public static float lengthSquared(
            @NotNull GeometryVector vector
    ) {
        return vector.x() * vector.x()
                + vector.y() * vector.y()
                + vector.z() * vector.z();
    }

    /**
     * Returns the length of a vector.
     *
     * @param vector the vector
     * @return the vector length
     */
    public static float length(
            @NotNull GeometryVector vector
    ) {
        return (float) Math.sqrt(
                lengthSquared(vector)
        );
    }

    /**
     * Normalizes a vector to unit length.
     *
     * <p>If the vector has zero length, a zero vector is returned.</p>
     *
     * @param vector the vector to normalize
     * @return the normalized vector
     */
    public static @NotNull GeometryVector normalize(
            @NotNull GeometryVector vector
    ) {
        float length =
                length(vector);

        if (length == 0.0F) {
            return new GeometryVector(
                    0.0F,
                    0.0F,
                    0.0F
            );
        }

        return new GeometryVector(
                vector.x() / length,
                vector.y() / length,
                vector.z() / length
        );
    }

    /**
     * Determines the dominant cardinal direction represented by
     * a vector.
     *
     * <p>This is primarily intended for validation and geometry
     * diagnostics. The vector does not need to be normalized.</p>
     *
     * <p>For example:</p>
     *
     * <pre>
     * ( 0,  1,  0) → UP
     * ( 0, -1,  0) → DOWN
     * ( 1,  0,  0) → EAST
     * (-1,  0,  0) → WEST
     * ( 0,  0,  1) → SOUTH
     * ( 0,  0, -1) → NORTH
     * </pre>
     *
     * @param vector the vector
     * @return the dominant cardinal direction
     * @throws IllegalArgumentException if the vector has zero length
     */
    public static @NotNull Direction dominantDirection(
            @NotNull GeometryVector vector
    ) {
        float absX =
                Math.abs(vector.x());

        float absY =
                Math.abs(vector.y());

        float absZ =
                Math.abs(vector.z());

        if (absX == 0.0F
                && absY == 0.0F
                && absZ == 0.0F) {

            throw new IllegalArgumentException(
                    "Cannot determine direction of a zero vector"
            );
        }

        if (absX >= absY && absX >= absZ) {
            return vector.x() >= 0.0F
                    ? Direction.EAST
                    : Direction.WEST;
        }

        if (absY >= absX && absY >= absZ) {
            return vector.y() >= 0.0F
                    ? Direction.UP
                    : Direction.DOWN;
        }

        return vector.z() >= 0.0F
                ? Direction.SOUTH
                : Direction.NORTH;
    }
}
