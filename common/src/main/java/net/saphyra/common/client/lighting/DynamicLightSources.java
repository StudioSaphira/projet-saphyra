package net.saphyra.common.client.lighting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.saphyra.common.api.lighting.GridLight;
import net.saphyra.common.platform.LightingPlatform;

/** Collects held lights separately from RGB propagation, inheriting the same item/block profiles. */
public final class DynamicLightSources {

    private DynamicLightSources() {}

    public static List<GridLight> collect(ClientLevel level, BlockPos origin, int size, LightingPlatform platform) {
        List<GridLight> lights = new ArrayList<>();
        for (var player : level.players()) {
            if (player.isSpectator()) continue;
            int x = (int) Math.floor(player.getX()) - origin.getX();
            int y = (int) Math.floor(player.getY() + 1.2) - origin.getY();
            int z = (int) Math.floor(player.getZ()) - origin.getZ();
            if (x < 0 || y < 0 || z < 0 || x >= size || y >= size || z >= size) continue;
            for (ItemStack stack : player.getHandSlots()) {
                var emission = platform.itemEmission(stack);
                if (emission.intensity() > 0) lights.add(new GridLight(x, y, z, emission, true));
            }
        }
        return List.copyOf(lights);
    }
}
