package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server packet to hurt multipart entities.
 */
public record MessageHurtMultipart(int part, int parent, float damage, String damageType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageHurtMultipart> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "hurt_multipart"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHurtMultipart> CODEC = new StreamCodec<>() {
        @Override
        public MessageHurtMultipart decode(RegistryFriendlyByteBuf buf) {
            int part = buf.readInt();
            int parent = buf.readInt();
            float damage = buf.readFloat();
            String damageType = buf.readUtf();
            return new MessageHurtMultipart(part, parent, damage, damageType);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageHurtMultipart packet) {
            buf.writeInt(packet.part);
            buf.writeInt(packet.parent);
            buf.writeFloat(packet.damage);
            buf.writeUtf(packet.damageType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    // Unified handler for bidirectional packet
    public static void handle(MessageHurtMultipart payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity part = player.level().getEntity(payload.part);
                Entity parent = player.level().getEntity(payload.parent);
                
                // Handle hurt/death time sync even without damage type
                if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                    ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, payload.damage, null);
                }
                
                // Only process damage if we have a valid damage type
                if (payload.damageType != null && !payload.damageType.isEmpty()) {
                    Registry<DamageType> registry = player.level().registryAccess().registry(Registries.DAMAGE_TYPE).get();
                    DamageType dmg = registry.get(ResourceLocation.parse(payload.damageType));
                    if (dmg != null) {
                        Holder<DamageType> holder = registry.getHolder(registry.getId(dmg)).orElse(null);
                        if (holder != null) {
                            DamageSource source = new DamageSource(holder);
                            if (part == null && parent != null && parent.isMultipartEntity()) {
                                parent.hurt(source, payload.damage);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void handleServer(MessageHurtMultipart payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity part = player.level().getEntity(payload.part);
                Entity parent = player.level().getEntity(payload.parent);
                
                // Handle hurt/death time sync even without damage type
                if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                    ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, payload.damage, null);
                }
                
                // Only process damage if we have a valid damage type
                if (payload.damageType != null && !payload.damageType.isEmpty()) {
                    Registry<DamageType> registry = player.level().registryAccess().registry(Registries.DAMAGE_TYPE).get();
                    DamageType dmg = registry.get(ResourceLocation.parse(payload.damageType));
                    if (dmg != null) {
                        Holder<DamageType> holder = registry.getHolder(registry.getId(dmg)).orElse(null);
                        if (holder != null) {
                            DamageSource source = new DamageSource(holder);
                            if (part == null && parent != null && parent.isMultipartEntity()) {
                                parent.hurt(source, payload.damage);
                            }
                        }
                    }
                }
            }
        });
    }
}
