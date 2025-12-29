package com.github.alexthe666.alexsmobs.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

/**
 * Armor materials for AlexsMobs.
 * Each material has a single layer pointing to its custom texture.
 */
public class AMArmorMaterials {
    
    // Helper to create armor materials with a custom texture layer
    private static Holder<ArmorMaterial> createMaterial(String textureName, Map<Type, Integer> defense, 
            int enchantmentValue, float toughness, float knockbackResistance) {
        return BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(
            new ArmorMaterial(
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("alexsmobs", textureName))),
                toughness,
                knockbackResistance
            )
        );
    }
    
    public static final Holder<ArmorMaterial> ROADRUNNER_ARMOR_MATERIAL = createMaterial(
        "roadrunner_boots", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 20, 0F, 0F);
    
    public static final Holder<ArmorMaterial> CROCODILE_ARMOR_MATERIAL = createMaterial(
        "crocodile_chestplate", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 7, Type.LEGGINGS, 5, Type.BOOTS, 2), 25, 1F, 0F);
    
    public static final Holder<ArmorMaterial> CENTIPEDE_ARMOR_MATERIAL = createMaterial(
        "centipede_leggings", Map.of(Type.HELMET, 6, Type.CHESTPLATE, 6, Type.LEGGINGS, 6, Type.BOOTS, 6), 22, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> MOOSE_ARMOR_MATERIAL = createMaterial(
        "moose_headgear", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 21, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> RACCOON_ARMOR_MATERIAL = createMaterial(
        "frontier_cap", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 21, 2.5F, 0F);
    
    public static final Holder<ArmorMaterial> SOMBRERO_ARMOR_MATERIAL = createMaterial(
        "sombrero", Map.of(Type.HELMET, 2, Type.CHESTPLATE, 2, Type.LEGGINGS, 2, Type.BOOTS, 2), 30, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = createMaterial(
        "spiked_turtle_shell", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 30, 1F, 0.2F);
    
    public static final Holder<ArmorMaterial> EMU_ARMOR_MATERIAL = createMaterial(
        "emu_leggings", Map.of(Type.HELMET, 4, Type.CHESTPLATE, 4, Type.LEGGINGS, 4, Type.BOOTS, 4), 20, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> FEDORA_ARMOR_MATERIAL = createMaterial(
        "fedora", Map.of(Type.HELMET, 2, Type.CHESTPLATE, 2, Type.LEGGINGS, 2, Type.BOOTS, 2), 30, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> TARANTULA_HAWK_ELYTRA_MATERIAL = createMaterial(
        "tarantula_hawk_elytra", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 5, 0F, 0F);
    
    public static final Holder<ArmorMaterial> FROSTSTALKER_ARMOR_MATERIAL = createMaterial(
        "froststalker_helmet", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 15, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> ROCKY_ARMOR_MATERIAL = createMaterial(
        "rocky_chestplate", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 7, Type.LEGGINGS, 5, Type.BOOTS, 2), 10, 0.5F, 0F);
    
    public static final Holder<ArmorMaterial> FLYING_FISH_MATERIAL = createMaterial(
        "flying_fish_boots", Map.of(Type.HELMET, 1, Type.CHESTPLATE, 1, Type.LEGGINGS, 1, Type.BOOTS, 1), 8, 0F, 0F);
    
    public static final Holder<ArmorMaterial> NOVELTY_HAT_MATERIAL = createMaterial(
        "novelty_hat", Map.of(Type.HELMET, 2, Type.CHESTPLATE, 2, Type.LEGGINGS, 2, Type.BOOTS, 2), 30, 0F, 0F);
    
    public static final Holder<ArmorMaterial> KIMONO_MATERIAL = createMaterial(
        "unsettling_kimono", Map.of(Type.HELMET, 3, Type.CHESTPLATE, 3, Type.LEGGINGS, 3, Type.BOOTS, 3), 15, 0F, 0F);
}
