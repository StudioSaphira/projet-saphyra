package net.saphyra.common.api.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.saphyra.common.api.geometry.GeometryVector;
import net.saphyra.common.api.geometry.GeometryVertex;
import net.saphyra.common.api.geometry.OutlineLine;

/** Client-only outline drawing using vanilla APIs; no loader-specific dependencies. */
public final class OutlineDrawing {
    private OutlineDrawing() {}

    /** Draws translucent black edges with an optional camera-relative translation. */
    public static void draw(PoseStack.Pose pose, VertexConsumer buffer,
                            List<OutlineLine> lines, float x, float y, float z) {
        for (OutlineLine line : lines) {
            GeometryVector normal = line.normal();
            vertex(pose, buffer, line.start(), normal, x, y, z);
            vertex(pose, buffer, line.end(), normal, x, y, z);
        }
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, GeometryVertex vertex,
                               GeometryVector normal, float x, float y, float z) {
        buffer.addVertex(pose, x + vertex.x(), y + vertex.y(), z + vertex.z())
                .setColor(0, 0, 0, 102).setNormal(pose, normal.x(), normal.y(), normal.z());
    }
}
