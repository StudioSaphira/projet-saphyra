package net.saphyra.neoforge.mixin.lighting;

import net.minecraft.client.multiplayer.ClientLevel;
import net.saphyra.common.client.lighting.LightingRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Invalidates the client opacity/source snapshot when server block updates arrive. */
@Mixin(ClientLevel.class)
public abstract class LightingChangesMixin {
    @Inject(method = "setBlocksDirty", at = @At("HEAD"))
    private void saphyra$dirty(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState,
            net.minecraft.world.level.block.state.BlockState newState, CallbackInfo ci) { LightingRuntime.invalidate(pos); }
    @Inject(method = "sendBlockUpdated", at = @At("HEAD"))
    private void saphyra$updated(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState,
            net.minecraft.world.level.block.state.BlockState newState, int flags, CallbackInfo ci) { LightingRuntime.invalidate(pos); }
}
