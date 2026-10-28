package net.saphyra.common.client.lighting;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;

/** GPU atlas of stable world sections. The page table locates a section regardless of viewer distance. */
public final class StaticLightAtlas implements AutoCloseable {

    private DynamicTexture atlas, pages;
    private int minX, minY, minZ, sizeX, sizeY, sizeZ, columns;

    /** Replaces one complete generation atomically on the render thread. */
    public void upload(Map<LightSectionKey, byte[]> sections) {
        if (sections.isEmpty()) { close(); return; }
        int dimension = 64;
        while ((dimension / 64) * (dimension / 64) < sections.size()) dimension *= 2;
        if (dimension > RenderSystem.maxSupportedTextureSize()) {
            throw new IllegalStateException("RGB atlas exceeds the GPU texture limit: " + dimension);
        }
        minX = sections.keySet().stream().mapToInt(LightSectionKey::x).min().orElseThrow();
        minY = sections.keySet().stream().mapToInt(LightSectionKey::y).min().orElseThrow();
        minZ = sections.keySet().stream().mapToInt(LightSectionKey::z).min().orElseThrow();
        sizeX = sections.keySet().stream().mapToInt(LightSectionKey::x).max().orElseThrow() - minX + 1;
        sizeY = sections.keySet().stream().mapToInt(LightSectionKey::y).max().orElseThrow() - minY + 1;
        sizeZ = sections.keySet().stream().mapToInt(LightSectionKey::z).max().orElseThrow() - minZ + 1;
        close();
        columns = dimension / 64;
        atlas = new DynamicTexture(dimension, dimension, true);
        pages = new DynamicTexture(sizeX, sizeY * sizeZ, true);
        int slot = 0;
        for (var entry : sections.entrySet()) {
            LightSectionKey key = entry.getKey();
            pages.getPixels().setPixelRGBA(key.x() - minX, key.y() - minY + sizeY * (key.z() - minZ), 0xFF000000 | (slot + 1));
            byte[] levels = entry.getValue();
            for (int z = 0; z < 16; z++) for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
                int i = 4 * (x + 16 * (y + 16 * z));
                int rgba = levels[i] * 17 | (levels[i+1] * 17 << 8) | (levels[i+2] * 17 << 16) | (levels[i+3] * 17 << 24);
                atlas.getPixels().setPixelRGBA((slot % columns) * 64 + (z % 4) * 16 + x,
                        (slot / columns) * 64 + (z / 4) * 16 + y, rgba);
            }
            slot++;
        }
        atlas.upload();
        pages.upload();
    }

    public boolean available() { return atlas != null; }

    /** Supplies world-relative coordinates, avoiding float precision loss far from world origin. */
    public void apply(ShaderInstance shader, Camera camera) {
        var enabled = shader.getUniform("SaphyraStaticEnabled");
        if (enabled == null) return;
        enabled.set(available() ? 1f : 0f);
        if (!available()) return;
        shader.setSampler("SaphyraStaticAtlas", atlas.getId());
        shader.setSampler("SaphyraStaticPages", pages.getId());
        shader.safeGetUniform("SaphyraStaticOffset").set((float)(camera.getPosition().x - minX * 16.0),
                (float)(camera.getPosition().y - minY * 16.0), (float)(camera.getPosition().z - minZ * 16.0));
        shader.safeGetUniform("SaphyraStaticSize").set((float)sizeX, (float)sizeY, (float)sizeZ);
        shader.safeGetUniform("SaphyraStaticColumns").set((float)columns);
    }

    @Override
    public void close() {
        if (atlas != null) { atlas.close(); atlas = null; }
        if (pages != null) { pages.close(); pages = null; }
    }
}
