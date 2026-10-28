package net.saphyra.common.api.rendering;

import java.util.function.Consumer;
import net.minecraft.client.renderer.block.model.BakedQuad;

/** Receives baked quads from the NeoForge rendering backend without exposing NeoForge types. */
public record BakedQuadTarget(Consumer<BakedQuad> output) implements FaceRenderTarget {}
