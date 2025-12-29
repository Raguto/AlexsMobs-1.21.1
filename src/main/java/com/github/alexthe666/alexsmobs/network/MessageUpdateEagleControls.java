package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server packet to update eagle controls.
 */
public record MessageUpdateEagleControls(int eagleId, float rotationYaw, float rotationPitch, boolean chunkLoad, int overEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateEagleControls> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "update_eagle_controls"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateEagleControls> CODEC = new StreamCodec<>() {
        @Override
        public MessageUpdateEagleControls decode(RegistryFriendlyByteBuf buf) {
            int eagleId = buf.readInt();
            float rotationYaw = buf.readFloat();
            float rotationPitch = buf.readFloat();
            boolean chunkLoad = buf.readBoolean();
            int overEntityId = buf.readInt();
            return new MessageUpdateEagleControls(eagleId, rotationYaw, rotationPitch, chunkLoad, overEntityId);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageUpdateEagleControls packet) {
            buf.writeInt(packet.eagleId);
            buf.writeFloat(packet.rotationYaw);
            buf.writeFloat(packet.rotationPitch);
            buf.writeBoolean(packet.chunkLoad);
            buf.writeInt(packet.overEntityId);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageUpdateEagleControls payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.eagleId);
                if (entity instanceof EntityBaldEagle) {
                    Entity over = null;
                    if (payload.overEntityId >= 0) {
                        over = player.level().getEntity(payload.overEntityId);
                    }
                    ((EntityBaldEagle) entity).directFromPlayer(payload.rotationYaw, payload.rotationPitch, payload.chunkLoad, over);
                }
            }
        });
    }
}
