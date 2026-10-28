package net.saphyra.common.api.lighting;

import java.util.List;

/** CPU RGB propagation through a frozen opacity grid. No world or graphics calls occur here. */
public final class LightGrid {

    private LightGrid() {}

    /** Returns interleaved R/G/B/custom-influence levels, each in the range 0..15. */
    public static byte[] propagate(int size, byte[] opacity, List<GridLight> sources) {
        int cells = size * size * size;
        if (opacity.length != cells) throw new IllegalArgumentException("Invalid opacity volume");
        byte[] result = new byte[cells * 4];
        int[] queue = new int[cells * 16];
        for (int channel = 0; channel < 4; channel++) {
            int head = 0, tail = 0;
            for (GridLight source : sources) {
                if (source.x() < 0 || source.y() < 0 || source.z() < 0
                        || source.x() >= size || source.y() >= size || source.z() >= size) continue;
                int color = source.emission().rgb();
                int component = channel == 3 ? (source.custom() ? 255 : 0) : (color >> (16 - channel * 8)) & 255;
                int strength = Math.round(source.emission().intensity() * component / 255f);
                int index = source.x() + size * (source.y() + size * source.z());
                if (strength > result[index * 4 + channel]) {
                    result[index * 4 + channel] = (byte) strength;
                    queue[tail++] = index;
                }
            }
            while (head < tail) {
                int index = queue[head++];
                int strength = result[index * 4 + channel];
                if (strength <= 1) continue;
                int x = index % size, y = (index / size) % size, z = index / (size * size);
                for (int direction = 0; direction < 6; direction++) {
                    int next = switch (direction) {
                        case 0 -> x > 0 ? index - 1 : -1;
                        case 1 -> x < size - 1 ? index + 1 : -1;
                        case 2 -> y > 0 ? index - size : -1;
                        case 3 -> y < size - 1 ? index + size : -1;
                        case 4 -> z > 0 ? index - size * size : -1;
                        default -> z < size - 1 ? index + size * size : -1;
                    };
                    if (next < 0) continue;
                    int value = strength - Math.max(1, opacity[next] & 255);
                    if (value > result[next * 4 + channel]) {
                        result[next * 4 + channel] = (byte) value;
                        queue[tail++] = next;
                    }
                }
            }
        }
        return result;
    }
}
