package net.saphyra.fabric.platform;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.saphyra.common.platform.PlatformServicesImpl;

public final class FabricPlatformServices implements PlatformServicesImpl {

    @Override public void sendReader(net.minecraft.server.level.ServerPlayer player,
                                      ReaderPacket packet) {
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player,packet);
    }

    @Override
    public void requestModelDataUpdate(
            BlockEntity blockEntity
    ) {
        /*
         * Fabric does not have NeoForge's ModelDataManager.
         *
         * Model data will be consumed directly by the
         * Fabric Copycat renderer.
         */
    }
}