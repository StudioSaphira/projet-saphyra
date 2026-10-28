package net.saphyra.common.client.lighting;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.saphyra.common.api.lighting.*;
import net.saphyra.common.platform.LightingPlatform;
import net.saphyra.common.platform.PlatformServices;
import org.joml.Matrix4f;

/** Client RGB volume manager. World reads and GPU uploads stay on the client thread. */
public final class LightingRuntime {

    public static final int SIZE = 64;
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "Saphyra RGB propagation");
        thread.setDaemon(true);
        return thread;
    });
    private static ClientLevel world;
    private static LightingSnapshot snapshot;
    private static CompletableFuture<LightingResult> pending;
    private static DynamicTexture texture;
    private static BlockPos textureOrigin;
    private static List<GridLight> mobile = List.of();
    private static int generation, ticks;
    private static boolean dirty = true, rendering;
    private static final Matrix4f inverseView = new Matrix4f();
    private static Camera camera;

    private LightingRuntime() {}

    /** Called by either loader's end-client-tick callback. */
    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != world) reset(minecraft.level);
        StaticLighting.tick(minecraft.level);
        if (world == null || minecraft.player == null) return;
        if (pending != null && pending.isDone()) {
            try {
                LightingResult result = pending.join();
                if (result.generation() == generation) upload(result);
            } catch (RuntimeException failure) {
                net.saphyra.common.constants.ModConstants.LOGGER.error("RGB propagation failed", failure);
            }
            pending = null;
        }
        if (++ticks % 2 != 0 || pending != null) return;
        var platform = PlatformServices.extension(LightingPlatform.class);
        BlockPos center = minecraft.player.blockPosition();
        BlockPos origin = new BlockPos(Math.floorDiv(center.getX(), 8) * 8 - 28,
                Math.floorDiv(center.getY(), 8) * 8 - 28, Math.floorDiv(center.getZ(), 8) * 8 - 28);
        boolean rebuild = dirty || snapshot == null || !origin.equals(snapshot.origin()) || ticks % 40 == 0;
        if (rebuild) {
            snapshot = capture(origin, platform);
            dirty = false;
        }
        List<GridLight> moving = DynamicLightSources.collect(world, origin, SIZE, platform);
        if (!rebuild && moving.equals(mobile)) return;
        mobile = moving;
        List<GridLight> sources = new ArrayList<>(snapshot.sources());
        sources.addAll(moving);
        byte[] opacity = snapshot.opacity();
        int epoch = generation;
        pending = CompletableFuture.supplyAsync(() ->
                new LightingResult(epoch, origin, LightGrid.propagate(SIZE, opacity, sources)), WORKER);
    }

    /** Refreshes sources and occluders after a block changes, including redstone lamp state. */
    public static void invalidate(BlockPos pos) {
        StaticLighting.invalidate(pos);
        dirty = true;
    }

    private static LightingSnapshot capture(BlockPos origin, LightingPlatform platform) {
        byte[] opacity = new byte[SIZE * SIZE * SIZE];
        List<GridLight> sources = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int z = 0; z < SIZE; z++) for (int x = 0; x < SIZE; x++) {
            boolean loaded = world.hasChunk((origin.getX() + x) >> 4, (origin.getZ() + z) >> 4);
            for (int y = 0; y < SIZE; y++) {
                int index = x + SIZE * (y + SIZE * z);
                pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                if (!loaded || world.isOutsideBuildHeight(pos)) { opacity[index] = 15; continue; }
                BlockState state = world.getBlockState(pos);
                opacity[index] = (byte) Math.clamp(state.getLightBlock(world, pos), 0, 15);
                LightEmission emission = platform.blockEmission(state, world, pos);
                if (emission.intensity() > 0) {
                    sources.add(new GridLight(x, y, z, emission, LightProfiles.block(state) != null));
                }
            }
        }
        return new LightingSnapshot(origin, opacity, List.copyOf(sources));
    }

    private static void upload(LightingResult result) {
        if (texture == null) texture = new DynamicTexture(512, 512, false);
        NativeImage image = texture.getPixels();
        if (image == null) return;
        byte[] levels = result.levels();
        for (int z = 0; z < SIZE; z++) for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) {
            int index = 4 * (x + SIZE * (y + SIZE * z));
            int rgba = (levels[index] * 17) | (levels[index + 1] * 17 << 8)
                    | (levels[index + 2] * 17 << 16) | (levels[index + 3] * 17 << 24);
            image.setPixelRGBA((z % 8) * SIZE + x, (z / 8) * SIZE + y, rgba);
        }
        texture.upload();
        textureOrigin = result.origin();
    }

    private static void reset(ClientLevel next) {
        generation++;
        world = next;
        snapshot = null;
        textureOrigin = null;
        mobile = List.of();
        dirty = true;
        rendering = false;
        if (texture != null) { texture.close(); texture = null; }
    }

    /** Captures the world-view transform so all supported core shaders sample the same volume. */
    public static void beginFrame(Camera activeCamera, Matrix4f view) {
        camera = activeCamera;
        inverseView.set(view).invert();
        rendering = true;
    }

    public static void endFrame() { rendering = false; }

    /** Supplies extra core-shader parameters; GUI and unavailable volumes retain exact vanilla lighting. */
    public static void apply(ShaderInstance shader) {
        var enabled = shader.getUniform("SaphyraLightEnabled");
        if (enabled == null) return;
        boolean dynamicAvailable = texture != null && textureOrigin != null;
        boolean active = rendering && camera != null && (dynamicAvailable || StaticLighting.ATLAS.available());
        enabled.set(active ? 1.0f : 0.0f);
        if (!active) return;
        StaticLighting.ATLAS.apply(shader, camera);
        shader.safeGetUniform("SaphyraDynamicEnabled").set(dynamicAvailable ? 1f : 0f);
        if (dynamicAvailable) shader.setSampler("SaphyraLightVolume", texture.getId());
        var offset = shader.getUniform("SaphyraCameraOffset");
        var view = shader.getUniform("SaphyraInverseView");
        if (offset != null && dynamicAvailable) offset.set((float) (camera.getPosition().x - textureOrigin.getX()),
                (float) (camera.getPosition().y - textureOrigin.getY()), (float) (camera.getPosition().z - textureOrigin.getZ()));
        if (view != null) view.set(inverseView);
    }
}
