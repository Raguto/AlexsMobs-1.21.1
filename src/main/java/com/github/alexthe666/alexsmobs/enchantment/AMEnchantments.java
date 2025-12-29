package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Resource keys for Alex's Mobs enchantments.
 * In 1.21, enchantments are data-driven and defined in JSON files.
 */
public class AMEnchantments {
    public static final ResourceKey<Enchantment> STRADDLE_JUMP = key("straddle_jump");
    public static final ResourceKey<Enchantment> LAVAWAX = key("lavawax");
    public static final ResourceKey<Enchantment> SERPENTFRIEND = key("serpentfriend");
    public static final ResourceKey<Enchantment> BOARD_RETURN = key("board_return");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }
}
