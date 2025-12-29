package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.data.TabIconDisplay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

public class ItemTabIcon extends ItemInventoryOnly {
    public ItemTabIcon(Item.Properties properties) {
        super(properties);
    }

    public static boolean hasCustomEntityDisplay(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY.get());
        return display != null && !display.displayEntityType().isEmpty();
    }

    public static String getCustomDisplayEntityString(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY.get());
        return display != null ? display.displayEntityType() : "";
    }

    public static int getDisplayMobFlags(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY.get());
        return display != null ? display.displayMobFlags() : 0;
    }

    public static float getDisplayMobScale(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY.get());
        return display != null ? display.displayMobScale() : 0.0f;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
    }

    @Nullable
    public static EntityType getEntityType(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY.get());
        if (display != null && !display.displayEntityType().isEmpty()) {
            return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(display.displayEntityType()));
        }
        return null;
    }

    // Legacy method for compatibility - reads from CompoundTag (used by renderer)
    @Nullable
    public static EntityType getEntityType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("DisplayEntityType")) {
            String entityType = tag.getString("DisplayEntityType");
            return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(entityType));
        }
        return null;
    }
}
