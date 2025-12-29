package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to set pupfish chunk location.
 */
public record MessageSetPupfishChunkOnClient(int chunkX, int chunkZ) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSetPupfishChunkOnClient> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "set_pupfish_chunk"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSetPupfishChunkOnClient> CODEC = new StreamCodec<>() {
        @Override
        public MessageSetPupfishChunkOnClient decode(RegistryFriendlyByteBuf buf) {
            int chunkX = buf.readInt();
            int chunkZ = buf.readInt();
            return new MessageSetPupfishChunkOnClient(chunkX, chunkZ);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSetPupfishChunkOnClient packet) {
            buf.writeInt(packet.chunkX);
            buf.writeInt(packet.chunkZ);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageSetPupfishChunkOnClient payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            AlexsMobs.PROXY.setPupfishChunkForItem(payload.chunkX, payload.chunkZ);
        });
    }
}
