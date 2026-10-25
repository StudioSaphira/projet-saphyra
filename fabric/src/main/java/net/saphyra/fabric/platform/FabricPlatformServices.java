package net.saphyra.fabric.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.saphyra.common.platform.PlatformServicesImpl;

/** Fabric implementation of Saphyra's shared services, independent of mod content. */
public class FabricPlatformServices implements PlatformServicesImpl {

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void requestModelDataUpdate(BlockEntity blockEntity) {
        // Fabric consumes render attachments directly and has no ModelDataManager to invalidate.
    }
}
