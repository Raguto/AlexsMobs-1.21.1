package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server packet to trigger transmutation from menu.
 */
public record MessageTransmuteFromMenu(int playerId, int choice) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageTransmuteFromMenu> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "transmute_from_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTransmuteFromMenu> CODEC = new StreamCodec<>() {
        @Override
        public MessageTransmuteFromMenu decode(RegistryFriendlyByteBuf buf) {
            int playerId = buf.readInt();
            int choice = buf.readInt();
            return new MessageTransmuteFromMenu(playerId, choice);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageTransmuteFromMenu packet) {
            buf.writeInt(packet.playerId);
            buf.writeInt(packet.choice);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageTransmuteFromMenu payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.getId() == payload.playerId && player.containerMenu instanceof MenuTransmutationTable table) {
                table.transmute(player, payload.choice);
            }
        });
    }
}
