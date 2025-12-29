package com.github.alexthe666.alexsmobs.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to sync entity position.
 */
public record MessageSyncEntityPos(int entityId, double x, double y, double z) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncEntityPos> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "sync_entity_pos"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncEntityPos> CODEC = new StreamCodec<>() {
        @Override
        public MessageSyncEntityPos decode(RegistryFriendlyByteBuf buf) {
            int entityId = buf.readInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new MessageSyncEntityPos(entityId, x, y, z);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSyncEntityPos packet) {
            buf.writeInt(packet.entityId);
            buf.writeDouble(packet.x);
            buf.writeDouble(packet.y);
            buf.writeDouble(packet.z);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageSyncEntityPos payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            Entity entity = level.getEntity(payload.entityId);
            if (entity instanceof com.github.alexthe666.alexsmobs.entity.IFalconry ||
                entity instanceof com.github.alexthe666.alexsmobs.entity.EntityStraddleboard) {
                entity.setPos(payload.x, payload.y, payload.z);
                entity.teleportTo(payload.x, payload.y, payload.z);
            }
        });
    }
}
