package net.saphyra.neoforge.compat.sodium;

import net.minecraft.resources.ResourceLocation;
import net.saphyra.common.client.compat.sodium.SodiumLightingShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Optional shader-source hook; string targets avoid linking Sodium when it is absent. */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader", remap = false)
public abstract class SodiumShaderLoaderMixin {
    @Inject(method = "getShaderSource", at = @At("RETURN"), cancellable = true, remap = false)
    private static void saphyra$rgbSource(ResourceLocation id, CallbackInfoReturnable<String> callback) {
        callback.setReturnValue(SodiumLightingShaders.patch(id, callback.getReturnValue()));
    }
}
