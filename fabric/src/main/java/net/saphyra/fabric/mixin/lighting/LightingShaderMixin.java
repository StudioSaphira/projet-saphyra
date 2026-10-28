package net.saphyra.fabric.mixin.lighting;

import net.minecraft.client.renderer.ShaderInstance;
import net.saphyra.common.client.lighting.LightingRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Populates optional Saphyra uniforms immediately before vanilla binds a core shader. */
@Mixin(ShaderInstance.class)
public abstract class LightingShaderMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    private void saphyra$lighting(CallbackInfo ci) {
        LightingRuntime.apply((ShaderInstance) (Object) this);
    }
}
