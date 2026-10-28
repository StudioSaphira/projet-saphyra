package net.saphyra.neoforge.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.saphyra.common.api.geometry.OutlineGeometry;
import net.saphyra.common.api.rendering.OutlineDrawing;
import net.saphyra.common.api.rendering.OutlineProviders;

/** Bridges NeoForge selection rendering to consumer-supplied block-local geometry. */
public final class NeoForgeOutlineEvents {

    private NeoForgeOutlineEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(NeoForgeOutlineEvents::render);
    }

    private static void render(RenderHighlightEvent.Block event) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var pos = event.getTarget().getBlockPos();
        var faces = OutlineProviders.faces(level, pos, level.getBlockState(pos));
        if (faces == null) return;
        var lines = OutlineGeometry.lines(faces);
        if (lines.isEmpty()) return;
        var camera = event.getCamera().getPosition();
        OutlineDrawing.draw(event.getPoseStack().last(), event.getMultiBufferSource().getBuffer(RenderType.lines()),
                lines, (float) (pos.getX() - camera.x), (float) (pos.getY() - camera.y), (float) (pos.getZ() - camera.z));
        event.setCanceled(true);
    }
}
