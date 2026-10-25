package net.saphyra.neoforge.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.saphyra.common.platform.PlatformServicesImpl;

/** NeoForge implementation of Saphyra's shared services, independent of mod content. */
public class NeoForgePlatformServices implements PlatformServicesImpl {

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void requestModelDataUpdate(BlockEntity blockEntity) {
        ((net.neoforged.neoforge.common.extensions.IBlockEntityExtension) blockEntity)
                .requestModelDataUpdate();
    }
}
