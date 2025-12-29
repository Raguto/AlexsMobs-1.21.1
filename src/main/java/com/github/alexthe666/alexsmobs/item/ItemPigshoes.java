package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
// TODO 1.21: EnchantmentCategory is now data-driven
// import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ItemPigshoes extends Item {

    public ItemPigshoes(Item.Properties props) {
        super(props);
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    // TODO 1.21: Enchantment API completely changed - enchantments are now data-driven
    // canApplyAtEnchantingTable method removed - use enchantment tags instead
}
