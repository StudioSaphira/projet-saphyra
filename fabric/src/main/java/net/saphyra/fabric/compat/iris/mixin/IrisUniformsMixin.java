package net.saphyra.fabric.compat.iris.mixin;

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

/** Updates shared RGB parameters after Iris updates a bound program's own uniforms. */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.gl.program.ProgramUniforms", remap = false)
public abstract class IrisUniformsMixin {

    @Unique private OpenGlLightingUniforms saphyra$uniforms;
    @Unique private int saphyra$program;

    @Inject(method = "update", at = @At("RETURN"))
    private void saphyra$update(CallbackInfo callback) {
        int program = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
        if (program == 0) return;
        if (saphyra$uniforms == null || saphyra$program != program) {
            saphyra$program = program;
            saphyra$uniforms = new OpenGlLightingUniforms(program, true);
        }
        LightingRuntime.apply(saphyra$uniforms);
    }
}
