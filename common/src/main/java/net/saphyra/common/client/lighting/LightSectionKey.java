package net.saphyra.common.client.lighting;

/** Stable world section address, independent of the viewer and of camera movement. */
public record LightSectionKey(int x, int y, int z) {
    public LightSectionKey offset(int dx, int dy, int dz) {
        return new LightSectionKey(x + dx, y + dy, z + dz);
    }
}
