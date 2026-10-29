package net.saphyra.fabric.compat.iris.mixin;

import com.google.common.collect.ImmutableList;
import net.saphyra.common.client.compat.iris.ShaderPackLighting;
import net.saphyra.common.client.compat.iris.IrisLightingBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adapts resolved shader sources in memory, after Iris has expanded includes. */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.include.IncludeProcessor", remap = false)
public abstract class IrisSourceMixin {

    @Inject(method = "getIncludedFile", at = @At("RETURN"), cancellable = true)
    private void saphyra$source(CallbackInfoReturnable<ImmutableList<String>> callback) {
        callback.setReturnValue(ShaderPackLighting.patch(callback.getReturnValue(), IrisLightingBindings.packName()));
    }
}
