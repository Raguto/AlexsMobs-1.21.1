package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectMosquitoRepellent extends MobEffect {

    public EffectMosquitoRepellent() {
        super(MobEffectCategory.BENEFICIAL, 0XCC7E70);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.mosquito_repellent";
    }
}
