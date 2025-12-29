package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AMPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> DEF_REG = DeferredRegister.create(net.minecraft.core.registries.Registries.PAINTING_VARIANT, AlexsMobs.MODID);

    public static final DeferredHolder<PaintingVariant, PaintingVariant> NFT = DEF_REG.register("nft", () -> new PaintingVariant(32, 32, net.minecraft.resources.ResourceLocation.parse("alexsmobs:nft")));
    public static final DeferredHolder<PaintingVariant, PaintingVariant> DOG_POKER = DEF_REG.register("dog_poker", () -> new PaintingVariant(32, 16, net.minecraft.resources.ResourceLocation.parse("alexsmobs:dog_poker")));
}
