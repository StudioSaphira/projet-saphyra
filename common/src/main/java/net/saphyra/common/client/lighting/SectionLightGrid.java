package net.saphyra.common.client.lighting;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Propagates RGB through sparse, world-addressed sections without a camera-distance cutoff. */
public final class SectionLightGrid {

    private SectionLightGrid() {}

    /** Computes independent channels and retains only sections influenced by a custom source. */
    public static Map<LightSectionKey, byte[]> propagate(List<LightSectionSnapshot> sections) {
        int count = sections.size();
        Map<LightSectionKey, Integer> indexes = new HashMap<>();
        for (int i = 0; i < count; i++) indexes.put(sections.get(i).key(), i);
        int[][] neighbors = new int[count][6];
        byte[][] values = new byte[count][4096 * 4];
        for (int i = 0; i < count; i++) {
            LightSectionKey key = sections.get(i).key();
            neighbors[i] = new int[] {
                indexes.getOrDefault(key.offset(-1,0,0), -1), indexes.getOrDefault(key.offset(1,0,0), -1),
                indexes.getOrDefault(key.offset(0,-1,0), -1), indexes.getOrDefault(key.offset(0,1,0), -1),
                indexes.getOrDefault(key.offset(0,0,-1), -1), indexes.getOrDefault(key.offset(0,0,1), -1)
            };
        }
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int channel = 0; channel < 4; channel++) {
            for (int i = 0; i < count; i++) {
                for (var source : sections.get(i).sources()) {
                    int component = channel == 3 ? (source.custom() ? 255 : 0)
                            : (source.emission().rgb() >> (16 - channel * 8)) & 255;
                    int strength = Math.round(source.emission().intensity() * component / 255f);
                    int cell = source.x() + 16 * (source.y() + 16 * source.z());
                    if (strength > values[i][cell * 4 + channel]) {
                        values[i][cell * 4 + channel] = (byte) strength;
                        queue.add(i * 4096 + cell);
                    }
                }
            }
            while (!queue.isEmpty()) {
                int encoded = queue.removeFirst(), section = encoded / 4096, cell = encoded % 4096;
                int strength = values[section][cell * 4 + channel];
                if (strength <= 1) continue;
                int x = cell & 15, y = (cell >> 4) & 15, z = cell >> 8;
                for (int direction = 0; direction < 6; direction++) {
                    boolean crosses = switch (direction) {
                        case 0 -> x == 0; case 1 -> x == 15; case 2 -> y == 0;
                        case 3 -> y == 15; case 4 -> z == 0; default -> z == 15;
                    };
                    int nextSection = crosses ? neighbors[section][direction] : section;
                    if (nextSection < 0) continue;
                    int nextCell = cell + switch (direction) {
                        case 0 -> crosses ? 15 : -1; case 1 -> crosses ? -15 : 1;
                        case 2 -> crosses ? 240 : -16; case 3 -> crosses ? -240 : 16;
                        case 4 -> crosses ? 3840 : -256; default -> crosses ? -3840 : 256;
                    };
                    int nextStrength = strength - Math.max(1, sections.get(nextSection).opacity()[nextCell] & 255);
                    if (nextStrength > values[nextSection][nextCell * 4 + channel]) {
                        values[nextSection][nextCell * 4 + channel] = (byte) nextStrength;
                        queue.add(nextSection * 4096 + nextCell);
                    }
                }
            }
        }
        Map<LightSectionKey, byte[]> result = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            for (int cell = 0; cell < 4096; cell++) {
                if (values[i][cell * 4 + 3] > 0) {
                    result.put(sections.get(i).key(), values[i]);
                    break;
                }
            }
        }
        return result;
    }
}
