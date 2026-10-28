package net.saphyra.common.api.geometry;

import net.minecraft.core.Direction;
import net.saphyra.common.api.geometry.GeometryUV;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.NotNull;

/**
 * Provides common operations for transforming normalized custom UV
 * coordinates.
 *
 * <p>This class contains only platform-independent UV calculations.
 * It does not depend on Fabric, NeoForge, texture sprites, or any
 * rendering API.</p>
 *
 * <p>All UV coordinates use the normalized range
 * {@code 0.0F} to {@code 1.0F}.</p>
 */
public final class UVMapping {

    private UVMapping() {
    }

    /**
     * Projects a vertex along a nominal face direction using the custom slope convention.
     * Horizontal faces use X/Z; side faces use height and a direction-dependent horizontal axis.
     * Returned coordinates are not clamped, allowing callers to choose their own UV bounds.
     */
    public static GeometryUV project(Direction direction, GeometryVertex vertex) {
        return switch (direction) {
            case DOWN, UP -> new GeometryUV(vertex.x(), vertex.z());
            case NORTH -> new GeometryUV(vertex.x(), 1.0F - vertex.y());
            case SOUTH -> new GeometryUV(1.0F - vertex.x(), 1.0F - vertex.y());
            case EAST -> new GeometryUV(1.0F - vertex.z(), 1.0F - vertex.y());
            case WEST -> new GeometryUV(vertex.z(), 1.0F - vertex.y());
        };
    }

    /**
     * Flips a UV coordinate horizontally.
     *
     * <p>The transformation is:</p>
     *
     * <pre>
     * U' = 1 - U
     * V' = V
     * </pre>
     *
     * @param uv the original UV coordinate
     * @return the horizontally flipped UV coordinate
     */
    public static @NotNull GeometryUV flipU(
            @NotNull GeometryUV uv
    ) {
        return new GeometryUV(
                1.0F - uv.u(),
                uv.v()
        );
    }

    /**
     * Flips a UV coordinate vertically.
     *
     * <p>The transformation is:</p>
     *
     * <pre>
     * U' = U
     * V' = 1 - V
     * </pre>
     *
     * @param uv the original UV coordinate
     * @return the vertically flipped UV coordinate
     */
    public static @NotNull GeometryUV flipV(
            @NotNull GeometryUV uv
    ) {
        return new GeometryUV(
                uv.u(),
                1.0F - uv.v()
        );
    }

    /**
     * Rotates a UV coordinate by 90-degree increments around the
     * center of the normalized UV square.
     *
     * <p>The rotation is clockwise:</p>
     *
     * <ul>
     *     <li>{@code 0} = 0°</li>
     *     <li>{@code 1} = 90°</li>
     *     <li>{@code 2} = 180°</li>
     *     <li>{@code 3} = 270°</li>
     * </ul>
     *
     * @param uv the original UV coordinate
     * @param rotations the number of clockwise 90-degree rotations
     * @return the rotated UV coordinate
     */
    public static @NotNull GeometryUV rotate(
            @NotNull GeometryUV uv,
            int rotations
    ) {
        int normalizedRotations =
                Math.floorMod(
                        rotations,
                        4
                );

        float u = uv.u();
        float v = uv.v();

        for (int i = 0; i < normalizedRotations; i++) {

            float previousU = u;

            u = 1.0F - v;
            v = previousU;
        }

        return new GeometryUV(
                u,
                v
        );
    }

    /**
     * Rotates a UV coordinate clockwise by 90 degrees.
     *
     * @param uv the original UV coordinate
     * @return the rotated UV coordinate
     */
    public static @NotNull GeometryUV rotateClockwise(
            @NotNull GeometryUV uv
    ) {
        return rotate(
                uv,
                1
        );
    }

    /**
     * Rotates a UV coordinate counter-clockwise by 90 degrees.
     *
     * @param uv the original UV coordinate
     * @return the rotated UV coordinate
     */
    public static @NotNull GeometryUV rotateCounterClockwise(
            @NotNull GeometryUV uv
    ) {
        return rotate(
                uv,
                3
        );
    }

    /**
     * Clamps a UV coordinate to the normalized range.
     *
     * <p>This is useful when calculations involving transformed
     * geometry produce values that are only marginally outside
     * the expected range because of floating-point precision.</p>
     *
     * @param uv the UV coordinate to clamp
     * @return the clamped UV coordinate
     */
    public static @NotNull GeometryUV clamp(
            @NotNull GeometryUV uv
    ) {
        return new GeometryUV(
                clamp(uv.u()),
                clamp(uv.v())
        );
    }

    /**
     * Clamps a normalized UV component to {@code 0.0F..1.0F}.
     *
     * @param value the UV component
     * @return the clamped value
     */
    private static float clamp(float value) {
        return Math.clamp(
                value,
                0.0F,
                1.0F);
    }
}
