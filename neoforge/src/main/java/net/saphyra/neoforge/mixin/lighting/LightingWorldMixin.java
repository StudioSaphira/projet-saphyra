package net.saphyra.neoforge.mixin.lighting;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.saphyra.common.client.lighting.LightingRuntime;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Restricts custom lighting to the world render pass, keeping GUI colors unchanged. */
@Mixin(LevelRenderer.class)
public abstract class LightingWorldMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void saphyra$begin(DeltaTracker delta, boolean outline, Camera camera, GameRenderer renderer,
                              LightTexture light, Matrix4f view, Matrix4f projection, CallbackInfo ci) {
        LightingRuntime.beginFrame(camera, view);
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void saphyra$end(CallbackInfo ci) { LightingRuntime.endFrame(); }
}
