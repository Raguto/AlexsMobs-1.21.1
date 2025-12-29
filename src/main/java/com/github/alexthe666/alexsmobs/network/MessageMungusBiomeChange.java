package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client packet to sync mungus biome transformation.
 */
public record MessageMungusBiomeChange(int mungusID, int posX, int posZ, String biomeOption) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageMungusBiomeChange> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("alexsmobs", "mungus_biome_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageMungusBiomeChange> CODEC = new StreamCodec<>() {
        @Override
        public MessageMungusBiomeChange decode(RegistryFriendlyByteBuf buf) {
            int mungusID = buf.readInt();
            int posX = buf.readInt();
            int posZ = buf.readInt();
            String biomeOption = buf.readUtf();
            return new MessageMungusBiomeChange(mungusID, posX, posZ, biomeOption);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageMungusBiomeChange packet) {
            buf.writeInt(packet.mungusID);
            buf.writeInt(packet.posX);
            buf.writeInt(packet.posZ);
            buf.writeUtf(packet.biomeOption);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageMungusBiomeChange payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.mungusID);
                Registry<Biome> registry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
                Biome biome = registry.get(ResourceLocation.parse(payload.biomeOption));
                ResourceKey<Biome> resourceKey = registry.getResourceKey(biome).orElse(null);
                Holder<Biome> holder = registry.getHolder(resourceKey).orElse(null);
                if (AMConfig.mungusBiomeTransformationType == 2) {
                    if (entity instanceof EntityMungus && entity.distanceToSqr(payload.posX, entity.getY(), payload.posZ) < 1000 && biome != null) {
                        LevelChunk chunk = player.level().getChunkAt(new BlockPos(payload.posX, 0, payload.posZ));
                        int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
                        int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
                        int l = Mth.clamp(QuartPos.fromBlock((int) entity.getY()), i, k);
                        int j = chunk.getSectionIndex(QuartPos.toBlock(l));
                        LevelChunkSection section = chunk.getSection(j);
                        if (section != null) {
                            PalettedContainer<Holder<Biome>> container = section.getBiomes().recreate();
                            for (int biomeX = 0; biomeX < 4; ++biomeX) {
                                for (int biomeY = 0; biomeY < 4; ++biomeY) {
                                    for (int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                                        container.getAndSetUnchecked(biomeX, biomeY, biomeZ, holder);
                                    }
                                }
                            }
                            // TODO 1.21: biomes field is private - need reflection or alternative approach
                            // section.biomes = container;
                        }
                        AlexsMobs.PROXY.updateBiomeVisuals(payload.posX, payload.posZ);
                    }
                }
            }
        });
    }
}
