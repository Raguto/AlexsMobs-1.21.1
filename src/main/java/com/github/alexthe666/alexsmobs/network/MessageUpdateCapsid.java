package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to update capsid inventory.
 */
public record MessageUpdateCapsid(long blockPos, ItemStack heldStack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateCapsid> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "update_capsid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateCapsid> CODEC = new StreamCodec<>() {
        @Override
        public MessageUpdateCapsid decode(RegistryFriendlyByteBuf buf) {
            long blockPos = buf.readLong();
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack heldStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageUpdateCapsid(blockPos, heldStack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageUpdateCapsid packet) {
            buf.writeLong(packet.blockPos);
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.heldStack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageUpdateCapsid payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                BlockPos pos = BlockPos.of(payload.blockPos);
                if (player.level().getBlockEntity(pos) instanceof TileEntityCapsid podium) {
                    podium.setItem(0, payload.heldStack);
                }
            }
        });
    }
}
