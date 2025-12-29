package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.item.ILeftClick;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server packet to trigger left-click behavior on items.
 */
public record MessageSwingArm() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSwingArm> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "swing_arm"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSwingArm> CODEC = new StreamCodec<>() {
        @Override
        public MessageSwingArm decode(RegistryFriendlyByteBuf buf) {
            return new MessageSwingArm();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageSwingArm packet) {
            // No data to encode
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MessageSwingArm payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                ItemStack leftItem = player.getItemInHand(InteractionHand.OFF_HAND);
                ItemStack rightItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (leftItem.getItem() instanceof ILeftClick) {
                    ((ILeftClick) leftItem.getItem()).onLeftClick(leftItem, player);
                }
                if (rightItem.getItem() instanceof ILeftClick) {
                    ((ILeftClick) rightItem.getItem()).onLeftClick(rightItem, player);
                }
            }
        });
    }
}
