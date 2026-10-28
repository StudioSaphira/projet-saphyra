package net.saphyra.common.api.geometry;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryVertex;
import org.jetbrains.annotations.Nullable;

/** Creates outward-wound rectangular faces in normalized block coordinates. */
public final class BoxGeometry {
    private BoxGeometry() {}
    /** Builds a box; an optional internal face can be omitted when combining components. */
    public static GeometryFace[] faces(float x1, float y1, float z1, float x2, float y2, float z2,
                                     @Nullable Direction omitted) {
        var a = new GeometryVertex(x1,y1,z1); var b = new GeometryVertex(x2,y1,z1);
        var c = new GeometryVertex(x1,y1,z2); var d = new GeometryVertex(x2,y1,z2);
        var e = new GeometryVertex(x1,y2,z1); var f = new GeometryVertex(x2,y2,z1);
        var g = new GeometryVertex(x1,y2,z2); var h = new GeometryVertex(x2,y2,z2);
        GeometryFace[] faces = {
                new GeometryFace(Direction.DOWN, new GeometryVertex[]{a,b,d,c}),
                new GeometryFace(Direction.UP, new GeometryVertex[]{e,g,h,f}),
                new GeometryFace(Direction.NORTH, new GeometryVertex[]{a,e,f,b}),
                new GeometryFace(Direction.SOUTH, new GeometryVertex[]{c,d,h,g}),
                new GeometryFace(Direction.WEST, new GeometryVertex[]{a,c,g,e}),
                new GeometryFace(Direction.EAST, new GeometryVertex[]{b,f,h,d})
        };
        return Arrays.stream(faces).filter(face -> face.direction() != omitted).toArray(GeometryFace[]::new);
    }
}
