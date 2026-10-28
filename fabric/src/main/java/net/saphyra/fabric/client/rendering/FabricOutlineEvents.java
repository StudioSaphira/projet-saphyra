package net.saphyra.fabric.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.saphyra.common.api.geometry.OutlineGeometry;
import net.saphyra.common.api.rendering.OutlineDrawing;
import net.saphyra.common.api.rendering.OutlineProviders;

/** Bridges Fabric selection rendering to consumer-supplied block-local geometry. */
public final class FabricOutlineEvents {

    private FabricOutlineEvents() {}

    @SuppressWarnings("deprecation")
    public static void register() {
        WorldRenderEvents.BLOCK_OUTLINE.register((context, outline) -> {
            var faces = OutlineProviders.faces(context.world(), outline.blockPos(), outline.blockState());
            if (faces == null) return true;
            var stack = context.matrixStack();
            var buffer = outline.vertexConsumer();
            if (stack == null || buffer == null) return true;
            var lines = OutlineGeometry.lines(faces);
            if (lines.isEmpty()) return true;
            var pos = outline.blockPos();
            OutlineDrawing.draw(stack.last(), buffer, lines,
                    (float) (pos.getX() - outline.cameraX()), (float) (pos.getY() - outline.cameraY()),
                    (float) (pos.getZ() - outline.cameraZ()));
            return false;
        });
    }
}
