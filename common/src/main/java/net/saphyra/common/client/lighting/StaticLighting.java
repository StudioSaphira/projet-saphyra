package net.saphyra.common.client.lighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.saphyra.common.api.lighting.GridLight;
import net.saphyra.common.api.lighting.LightProfiles;
import net.saphyra.common.platform.LightingPlatform;
import net.saphyra.common.platform.PlatformServices;

/** Retains fixed RGB light in all relevant loaded sections; moving the camera never fades cached colors. */
public final class StaticLighting {

    public static final StaticLightAtlas ATLAS = new StaticLightAtlas();
    private static final java.util.concurrent.ExecutorService WORKER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "Saphyra static RGB");
        thread.setDaemon(true);
        return thread;
    });
    private static final Map<LightSectionKey, LightSectionSnapshot> CACHE = new HashMap<>();
    private static final Map<LightSectionKey, LevelChunkSection> IDENTITIES = new HashMap<>();
    private static ClientLevel world;
    private static CompletableFuture<Map<LightSectionKey, byte[]>> pending;
    private static Set<LightSectionKey> previous = Set.of();
    private static int ticks, revision, builtRevision = -1, pendingRevision;

    private StaticLighting() {}

    /** Invalidates just the changed section; nearby light propagation is recomputed from cached inputs. */
    public static void invalidate(BlockPos pos) {
        if (CACHE.remove(new LightSectionKey(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4)) != null) revision++;
    }

    /** Drops dimension-specific CPU and GPU state on disconnect or world replacement. */
    public static void reset(ClientLevel next) {
        world = next;
        CACHE.clear();
        IDENTITIES.clear();
        previous = Set.of();
        pending = null;
        builtRevision = -1;
        revision++;
        ATLAS.close();
    }

    /** Scans palettes for custom emitters, then snapshots only their influence area plus a vanilla-light halo. */
    public static void tick(ClientLevel level) {
        if (level != world) reset(level);
        if (world == null) return;
        if (pending != null && pending.isDone()) {
            try {
                var result = pending.join();
                if (pendingRevision == revision) ATLAS.upload(result);
                else builtRevision = -1;
            } catch (RuntimeException failure) {
                net.saphyra.common.constants.ModConstants.LOGGER.error("Static RGB propagation failed", failure);
                builtRevision = -1;
            }
            pending = null;
        }
        if (++ticks % 10 != 0 || pending != null || Minecraft.getInstance().player == null) return;
        var platform = PlatformServices.extension(LightingPlatform.class);
        int radius = Minecraft.getInstance().options.getEffectiveRenderDistance() + 3;
        BlockPos center = Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition();
        Set<LightSectionKey> sources = new HashSet<>();
        Map<LightSectionKey, LevelChunkSection> loaded = new HashMap<>();
        for (int cz = (center.getZ() >> 4) - radius; cz <= (center.getZ() >> 4) + radius; cz++) {
            for (int cx = (center.getX() >> 4) - radius; cx <= (center.getX() >> 4) + radius; cx++) {
                if (!world.hasChunk(cx, cz)) continue;
                var sections = world.getChunk(cx, cz).getSections();
                for (int i = 0; i < sections.length; i++) {
                    var key = new LightSectionKey(cx, world.getSectionYFromSectionIndex(i), cz);
                    var section = sections[i];
                    loaded.put(key, section);
                    if (IDENTITIES.put(key, section) != section) { CACHE.remove(key); revision++; }
                    if (section.maybeHas(LightProfiles::hasBlockProfile)) {
                        var snapshot = snapshot(key, section, platform);
                        if (snapshot.sources().stream().anyMatch(GridLight::custom)) sources.add(key);
                    }
                }
            }
        }
        IDENTITIES.keySet().retainAll(loaded.keySet());
        CACHE.keySet().retainAll(loaded.keySet());
        Set<LightSectionKey> inputs = new HashSet<>();
        for (var key : sources) for (int z = -2; z <= 2; z++) for (int y = -2; y <= 2; y++) for (int x = -2; x <= 2; x++) {
            var neighbor = key.offset(x, y, z);
            if (loaded.containsKey(neighbor)) inputs.add(neighbor);
        }
        if (inputs.equals(previous) && builtRevision == revision) return;
        List<LightSectionSnapshot> snapshots = new ArrayList<>(inputs.size());
        for (var key : inputs) snapshots.add(snapshot(key, loaded.get(key), platform));
        previous = Set.copyOf(inputs);
        builtRevision = pendingRevision = revision;
        pending = CompletableFuture.supplyAsync(() -> SectionLightGrid.propagate(snapshots), WORKER);
    }

    private static LightSectionSnapshot snapshot(LightSectionKey key, LevelChunkSection section, LightingPlatform platform) {
        var existing = CACHE.get(key);
        if (existing != null) return existing;
        byte[] opacity = new byte[4096];
        List<GridLight> sources = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (!section.hasOnlyAir()) {
            for (int z = 0; z < 16; z++) for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
                var state = section.getBlockState(x, y, z);
                pos.set(key.x() * 16 + x, key.y() * 16 + y, key.z() * 16 + z);
                opacity[x + 16 * (y + 16 * z)] = (byte)Math.clamp(state.getLightBlock(world, pos), 0, 15);
                var emission = platform.blockEmission(state, world, pos);
                if (emission.intensity() > 0) sources.add(new GridLight(x, y, z, emission, LightProfiles.hasBlockProfile(state)));
            }
        }
        var result = new LightSectionSnapshot(key, opacity, List.copyOf(sources));
        CACHE.put(key, result);
        return result;
    }
}
