package net.saphyra.neoforge.compat.iris.mixin;

import net.saphyra.common.client.compat.iris.IrisLightingBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adds RGB textures through Iris's allocator after the pack's own sampler registrations. */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.gl.program.ProgramSamplers$Builder", remap = false)
public abstract class IrisSamplersMixin {

    @Inject(method = "build", at = @At("HEAD"))
    private void saphyra$samplers(CallbackInfoReturnable<Object> callback) {
        IrisLightingBindings.register(this);
    }
}
