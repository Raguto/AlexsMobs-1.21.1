package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EffectPoisonResistance extends MobEffect {

    public EffectPoisonResistance() {
        super(MobEffectCategory.BENEFICIAL, 0X51FFAF);

    }

    public boolean applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
        if(LivingEntityIn.hasEffect(MobEffects.POISON)){
            LivingEntityIn.removeEffect(MobEffects.POISON);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.poison_resistance";
    }

}
