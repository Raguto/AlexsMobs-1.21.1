package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to mount entities on players.
 */
public record MessageMosquitoMountPlayer(int rider, int mount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageMosquitoMountPlayer> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "mosquito_mount_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMosquitoMountPlayer> CODEC = new StreamCodec<>() {
        @Override
        public MessageMosquitoMountPlayer decode(RegistryFriendlyByteBuf buf) {
            int rider = buf.readInt();
            int mount = buf.readInt();
            return new MessageMosquitoMountPlayer(rider, mount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageMosquitoMountPlayer packet) {
            buf.writeInt(packet.rider);
            buf.writeInt(packet.mount);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageMosquitoMountPlayer payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.rider);
                Entity mountEntity = player.level().getEntity(payload.mount);
                if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage
                        || entity instanceof EntityBaldEagle)
                        && mountEntity instanceof Player && entity.distanceTo(mountEntity) < 16D) {
                    entity.startRiding(mountEntity, true);
                }
            }
        });
    }
}
