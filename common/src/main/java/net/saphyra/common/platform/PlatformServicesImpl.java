package net.saphyra.common.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Shared loader operations. Payload registration and payload meaning belong to each consuming mod.
 * Common exposes only Java and vanilla Minecraft types.
 */
public interface PlatformServicesImpl {

    /** Sends a payload whose type was registered by its owning mod for this connection. */
    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    /** Invalidates loader-specific model data after a block entity changes on the client. */
    void requestModelDataUpdate(BlockEntity blockEntity);
}
