package net.saphyra.common.client.lighting;

import java.util.List;
import net.saphyra.common.api.lighting.GridLight;

/** Immutable input arrays owned by the client snapshot cache and read by the propagation worker. */
public record LightSectionSnapshot(LightSectionKey key, byte[] opacity, List<GridLight> sources) {}
