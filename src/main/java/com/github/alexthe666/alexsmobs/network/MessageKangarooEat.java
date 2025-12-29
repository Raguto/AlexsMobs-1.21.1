package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to display kangaroo eating particles.
 */
public record MessageKangarooEat(int kangaroo, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageKangarooEat> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "kangaroo_eat"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageKangarooEat> CODEC = new StreamCodec<>() {
        @Override
        public MessageKangarooEat decode(RegistryFriendlyByteBuf buf) {
            int kangaroo = buf.readInt();
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new MessageKangarooEat(kangaroo, stack);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageKangarooEat packet) {
            buf.writeInt(packet.kangaroo);
            // Use OPTIONAL_STREAM_CODEC to allow empty ItemStacks
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageKangarooEat payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.kangaroo);
                if (entity instanceof EntityKangaroo kangaroo && kangaroo.kangarooInventory != null) {
                    for (int i = 0; i < 7; i++) {
                        double d2 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        double d0 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        double d1 = kangaroo.getRandom().nextGaussian() * 0.02D;
                        entity.level().addParticle(
                            new ItemParticleOption(ParticleTypes.ITEM, payload.stack),
                            entity.getX() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F,
                            entity.getY() + entity.getBbHeight() * 0.5F + (double) (kangaroo.getRandom().nextFloat() * entity.getBbHeight() * 0.5F),
                            entity.getZ() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F,
                            d0, d1, d2
                        );
                    }
                }
            }
        });
    }
}
