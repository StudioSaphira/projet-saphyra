package net.saphyra.common.client.lighting;

import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.saphyra.common.api.lighting.*;

/** TEMPORARY local validation fixture; removed before distribution. */
public final class LightingDemo {
    private static int ticks;
    private static boolean initialized;
    public static void tick() {
        if (!initialized) {
            initialized = true;
            SaphyraLighting.registerBlock(Blocks.SEA_LANTERN, s -> new LightEmission(0x0000FF,15));
            SaphyraLighting.registerBlock(Blocks.REDSTONE_LAMP, s -> s.getValue(BlockStateProperties.LIT) ? new LightEmission(0xFF0000,15) : LightEmission.NONE);
            SaphyraLighting.registerBlock(Blocks.GLOWSTONE, s -> new LightEmission(0xFFFF00,15));
            SaphyraLighting.registerBlock(Blocks.VERDANT_FROGLIGHT, s -> new LightEmission(0x00FF00,15));
            SaphyraLighting.registerBlock(Blocks.PEARLESCENT_FROGLIGHT, s -> new LightEmission(0xFF00FF,15));
        }
        var mc = Minecraft.getInstance();
        if (++ticks % 20 != 0 || mc.getSingleplayerServer() == null || mc.player == null) return;
        var server = mc.getSingleplayerServer();
        if (!server.getWorldData().getLevelName().equals("New WorldLighting Validation")) return;
        Path file = Path.of("../../build/lighting-control.txt");
        try {
            if (!Files.exists(file)) return;
            var lines = Files.readAllLines(file);
            Files.delete(file);
            server.execute(() -> {
                for (String command : lines) if (!command.isBlank()) server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withPermission(4), command);
            });
        } catch (Exception exception) { exception.printStackTrace(); }
    }
}


