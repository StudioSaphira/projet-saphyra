package net.saphyra.neoforge.compat.sodium;

import net.saphyra.common.client.lighting.LightingRuntime;
import net.saphyra.common.client.lighting.shader.OpenGlLightingUniforms;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Binds the shared light fields only to patched programs and restores GL state afterward. */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.GlProgram", remap = false)
public abstract class SodiumProgramMixin {
    @Unique
    private OpenGlLightingUniforms saphyra$uniforms;

    @Inject(method = "bind", at = @At("RETURN"), remap = false)
    private void saphyra$bindRgb(CallbackInfo callback) {
        if (saphyra$uniforms == null) saphyra$uniforms = new OpenGlLightingUniforms(GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM));
        LightingRuntime.apply(saphyra$uniforms);
    }

    @Inject(method = {"unbind", "delete"}, at = @At("HEAD"), remap = false)
    private void saphyra$restoreRgb(CallbackInfo callback) {
        if (saphyra$uniforms != null) saphyra$uniforms.restore();
    }
}
