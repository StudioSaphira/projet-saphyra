package net.saphyra.common.api.geometry;

import java.util.ArrayList;
import java.util.List;
import net.saphyra.common.api.geometry.GeometryFace;
import net.saphyra.common.api.geometry.GeometryVertex;
import net.saphyra.common.api.geometry.OutlineLine;

/** Extracts polygon edges independently of Fabric, NeoForge and client rendering classes. */
public final class OutlineGeometry {
    private static final float EPSILON = 1.0E-5F;
    private OutlineGeometry() {}

    /**
     * Extracts nonzero edges in face order. Shared edges are deliberately preserved:
     * removing edges between faces with different normals would erase silhouette edges.
     */
    public static List<OutlineLine> lines(GeometryFace[] faces) {
        List<OutlineLine> lines = new ArrayList<>();
        for (GeometryFace face : faces) {
            GeometryVertex[] vertices = face.vertices();
            if (vertices.length < 3) continue;
            for (int i = 0; i < vertices.length; i++) {
                GeometryVertex start = vertices[i];
                GeometryVertex end = vertices[(i + 1) % vertices.length];
                if (Math.abs(start.x() - end.x()) < EPSILON
                        && Math.abs(start.y() - end.y()) < EPSILON
                        && Math.abs(start.z() - end.z()) < EPSILON) continue;
                lines.add(new OutlineLine(start, end, face.direction()));
            }
        }
        return lines;
    }
}
