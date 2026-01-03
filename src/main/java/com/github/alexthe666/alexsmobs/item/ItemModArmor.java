package com.github.alexthe666.alexsmobs.item;

import static com.github.alexthe666.alexsmobs.item.AMArmorMaterials.*;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Custom armor item for AlexsMobs.
 * Custom armor rendering is registered via RegisterClientExtensionsEvent in ClientProxy.
 * Textures are provided via getArmorTexture override.
 */
public class ItemModArmor extends ArmorItem {
    
    public ItemModArmor(Holder<ArmorMaterial> material, Type type) {
        super(material, type, new Item.Properties().stacksTo(1));
    }
    
    public ItemModArmor(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties.stacksTo(1));
    }
    
    @Override
    public int getEnchantmentValue() {
        return 15;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }
    
    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        Holder<ArmorMaterial> mat = this.getMaterial();
        
        if (mat == CROCODILE_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/crocodile_chestplate.png");
        } else if (mat == ROADRUNNER_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/roadrunner_boots.png");
        } else if (mat == CENTIPEDE_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/centipede_leggings.png");
        } else if (mat == MOOSE_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/moose_headgear.png");
        } else if (mat == RACCOON_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/frontier_cap.png");
        } else if (mat == SOMBRERO_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/sombrero.png");
        } else if (mat == SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/spiked_turtle_shell.png");
        } else if (mat == FEDORA_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/fedora.png");
        } else if (mat == EMU_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/emu_leggings.png");
        } else if (mat == FROSTSTALKER_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/froststalker_helmet.png");
        } else if (mat == ROCKY_ARMOR_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/rocky_chestplate.png");
        } else if (mat == FLYING_FISH_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/flying_fish_boots.png");
        } else if (mat == NOVELTY_HAT_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/novelty_hat.png");
        } else if (mat == KIMONO_MATERIAL) {
            return ResourceLocation.fromNamespaceAndPath("alexsmobs", "textures/armor/unsettling_kimono.png");
        }
        
        return null;
    }
}
