package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.IDancingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to sync dancing state.
 */
public record MessageStartDancing(int entityID, boolean dance, BlockPos jukeBox) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageStartDancing> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "start_dancing"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageStartDancing> CODEC = new StreamCodec<>() {
        @Override
        public MessageStartDancing decode(RegistryFriendlyByteBuf buf) {
            int entityID = buf.readInt();
            boolean dance = buf.readBoolean();
            BlockPos jukeBox = buf.readBlockPos();
            return new MessageStartDancing(entityID, dance, jukeBox);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageStartDancing packet) {
            buf.writeInt(packet.entityID);
            buf.writeBoolean(packet.dance);
            buf.writeBlockPos(packet.jukeBox);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageStartDancing payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.entityID);
                if (entity instanceof IDancingMob dancingMob) {
                    dancingMob.setDancing(payload.dance);
                    if (payload.dance) {
                        dancingMob.setJukeboxPos(payload.jukeBox);
                    } else {
                        dancingMob.setJukeboxPos(null);
                    }
                }
            }
        });
    }
}
