package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to dismount crow from player.
 */
public record MessageCrowDismount(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageCrowDismount> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "crow_dismount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCrowDismount> CODEC = new StreamCodec<>() {
        @Override
        public MessageCrowDismount decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageCrowDismount(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageCrowDismount packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageCrowDismount payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.rider);
                Entity mountEntity = player.level().getEntity(payload.mount);
                if (entity instanceof EntityCrow && mountEntity != null) {
                    entity.stopRiding();
                }
            }
        });
    }
}
