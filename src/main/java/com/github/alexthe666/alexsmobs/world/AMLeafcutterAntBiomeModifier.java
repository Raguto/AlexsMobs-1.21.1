package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AMLeafcutterAntBiomeModifier implements BiomeModifier {
    public static final MapCodec<AMLeafcutterAntBiomeModifier> CODEC = RecordCodecBuilder.mapCodec((config) -> {
        return config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter((otherConfig) -> {
            return otherConfig.features;
        })).apply(config, AMLeafcutterAntBiomeModifier::new);
    });
    private final HolderSet<PlacedFeature> features;

    public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
        this.features = features;
    }

    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            AMWorldRegistry.addLeafcutterAntSpawns(biome, this.features, builder);
        }
    }

    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    public static MapCodec<AMLeafcutterAntBiomeModifier> makeCodec() {
        return CODEC;
    }
}
