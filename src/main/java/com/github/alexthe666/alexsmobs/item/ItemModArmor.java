package com.github.alexthe666.alexsmobs.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

/**
 * Stub class for mod armor items.
 * TODO 1.21: Armor materials need to be registered properly using data-driven approach.
 */
public class ItemModArmor extends ArmorItem {
    
    public ItemModArmor(Holder<ArmorMaterial> material, Type type) {
        super(material, type, new Item.Properties());
    }
    
    public ItemModArmor(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties);
    }
}
