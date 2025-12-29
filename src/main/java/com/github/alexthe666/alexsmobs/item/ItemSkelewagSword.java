package com.github.alexthe666.alexsmobs.item;

import net.neoforged.neoforge.common.ItemAbilities;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
// TODO 1.21: ToolAction system changed
// import net.neoforged.neoforge.common.ToolAction;
import net.minecraft.world.item.component.Tool;

public class ItemSkelewagSword extends SwordItem {

    private final ImmutableMultimap<net.minecraft.core.Holder<Attribute>, AttributeModifier> skelewagModifiers;

    public ItemSkelewagSword(Item.Properties props) {
        super(Tiers.IRON, props);
        ImmutableMultimap.Builder<net.minecraft.core.Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(net.minecraft.resources.ResourceLocation.parse("alexsmobs:attack_damage"), (double)3.5F, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(net.minecraft.resources.ResourceLocation.parse("alexsmobs:attack_speed"), (double)0, AttributeModifier.Operation.ADD_VALUE));
        this.skelewagModifiers = builder.build();
    }

    public float getDamage() {
        return 3.5F;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility toolAction) {
        return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack lvt_4_1_ = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(lvt_4_1_);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Items.BONE);
    }

    // TODO 1.21: getDefaultAttributeModifiers signature changed
    // Use Item.Properties.attributes() instead

}
