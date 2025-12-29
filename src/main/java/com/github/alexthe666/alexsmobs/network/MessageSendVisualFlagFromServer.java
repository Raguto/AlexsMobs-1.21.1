package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to process visual flags on entities.
 */
public record MessageSendVisualFlagFromServer(int entityID, int flag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSendVisualFlagFromServer> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "send_visual_flag"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSendVisualFlagFromServer> CODEC = new StreamCodec<>() {
        @Override
        public MessageSendVisualFlagFromServer decode(RegistryFriendlyByteBuf buf) {
            int entityID = buf.readInt();
            int flag = buf.readInt();
            return new MessageSendVisualFlagFromServer(entityID, flag);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSendVisualFlagFromServer packet) {
            buf.writeInt(packet.entityID);
            buf.writeInt(packet.flag);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageSendVisualFlagFromServer payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.entityID);
                AlexsMobs.PROXY.processVisualFlag(entity, payload.flag);
            }
        });
    }
}
