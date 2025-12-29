package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to update transmutation display options.
 */
public record MessageUpdateTransmutablesToDisplay(int playerId, ItemStack stack1, ItemStack stack2, ItemStack stack3) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateTransmutablesToDisplay> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "update_transmutables"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateTransmutablesToDisplay> CODEC = new StreamCodec<>() {
        @Override
        public MessageUpdateTransmutablesToDisplay decode(RegistryFriendlyByteBuf buf) {
            int playerId = buf.readInt();
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack stack1 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            ItemStack stack2 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            ItemStack stack3 = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageUpdateTransmutablesToDisplay(playerId, stack1, stack2, stack3);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageUpdateTransmutablesToDisplay packet) {
            buf.writeInt(packet.playerId);
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack1);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack2);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack3);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageUpdateTransmutablesToDisplay payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.getId() == payload.playerId) {
                AlexsMobs.PROXY.setDisplayTransmuteResult(0, payload.stack1);
                AlexsMobs.PROXY.setDisplayTransmuteResult(1, payload.stack2);
                AlexsMobs.PROXY.setDisplayTransmuteResult(2, payload.stack3);
            }
        });
    }
}
