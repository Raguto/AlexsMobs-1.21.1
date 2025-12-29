package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to mount crow on player.
 */
public record MessageCrowMountPlayer(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageCrowMountPlayer> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "crow_mount_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCrowMountPlayer> CODEC = new StreamCodec<>() {
        @Override
        public MessageCrowMountPlayer decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageCrowMountPlayer(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageCrowMountPlayer packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageCrowMountPlayer payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.rider);
                Entity mountEntity = player.level().getEntity(payload.mount);
                if (entity instanceof EntityCrow && mountEntity instanceof Player
                        && entity.distanceTo(mountEntity) < 16D) {
                    entity.startRiding(mountEntity, true);
                }
            }
        });
    }
}
