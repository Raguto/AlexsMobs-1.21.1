package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Armor materials for AlexsMobs.
 * TODO 1.21: These need to be registered via data pack or proper registry.
 * For now, using vanilla leather as placeholder.
 */
public class AMArmorMaterials {
    
    // Placeholder - all armor uses leather material for now
    // TODO: Register proper armor materials via data pack
    public static final Holder<ArmorMaterial> ROADRUNNER_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> CROCODILE_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> CENTIPEDE_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> MOOSE_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> RACCOON_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> SOMBRERO_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("turtle")).orElseThrow();
    public static final Holder<ArmorMaterial> EMU_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> FEDORA_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> TARANTULA_HAWK_ELYTRA_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> FROSTSTALKER_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> ROCKY_ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("iron")).orElseThrow();
    public static final Holder<ArmorMaterial> FLYING_FISH_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> NOVELTY_HAT_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
    public static final Holder<ArmorMaterial> KIMONO_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.getHolder(ResourceLocation.withDefaultNamespace("leather")).orElseThrow();
}
