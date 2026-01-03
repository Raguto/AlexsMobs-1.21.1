package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectKnockbackResistance extends MobEffect {

    public EffectKnockbackResistance() {
        super(MobEffectCategory.BENEFICIAL, 0X865337);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, net.minecraft.resources.ResourceLocation.parse("alexsmobs:knockback_resistance"), 0.5D, AttributeModifier.Operation.ADD_VALUE);
    }

    public boolean applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.knockback_resistance";
    }

}
