package net.saphyra.common.api.geometry;

import net.saphyra.common.api.geometry.GeometryTransforms;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Rotates and reflects custom voxel shapes in normalized block coordinates. */
public final class ShapeTransforms {
    private ShapeTransforms() {}

    /** Rotates a NORTH-oriented shape clockwise around the block center. */
    public static VoxelShape rotateHorizontal(VoxelShape shape, Direction facing) {
        int rotations = GeometryTransforms.rotationCount(facing);
        VoxelShape result = shape;
        for (int i = 0; i < rotations; i++) {
            VoxelShape[] rotated = {Shapes.empty()};
            result.forAllBoxes((x1, y1, z1, x2, y2, z2) -> rotated[0] = Shapes.or(
                    rotated[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            result = rotated[0];
        }
        return result;
    }

    /** Reflects the shape around Y = 0.5 without changing its horizontal orientation. */
    public static VoxelShape mirrorVertical(VoxelShape shape) {
        VoxelShape[] result = {Shapes.empty()};
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> result[0] = Shapes.or(
                result[0], Shapes.box(x1, 1 - y2, z1, x2, 1 - y1, z2)));
        return result[0];
    }
}
