package net.saphyra.fabric.client.rendering;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.core.Direction;
import net.saphyra.common.api.rendering.FaceRenderTarget;
import org.jetbrains.annotations.Nullable;

/** Fabric destination retaining the caller's material and culling policy. */
public record FabricFaceTarget(QuadEmitter emitter, RenderMaterial material,
                                @Nullable Direction nominalFace) implements FaceRenderTarget {}
