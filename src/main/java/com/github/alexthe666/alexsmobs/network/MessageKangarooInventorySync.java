package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to sync kangaroo inventory.
 */
public record MessageKangarooInventorySync(int kangaroo, int slotId, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageKangarooInventorySync> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "kangaroo_inventory_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageKangarooInventorySync> CODEC = new StreamCodec<>() {
        @Override
        public MessageKangarooInventorySync decode(RegistryFriendlyByteBuf buf) {
            int kangaroo = buf.readInt();
            int slotId = buf.readInt();
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageKangarooInventorySync(kangaroo, slotId, stack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageKangarooInventorySync packet) {
            buf.writeInt(packet.kangaroo);
            buf.writeInt(packet.slotId);
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageKangarooInventorySync payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.kangaroo);
                if (entity instanceof EntityKangaroo kangaroo && kangaroo.kangarooInventory != null) {
                    if (payload.slotId >= 0) {
                        kangaroo.kangarooInventory.setItem(payload.slotId, payload.stack);
                    }
                }
            }
        });
    }
}
