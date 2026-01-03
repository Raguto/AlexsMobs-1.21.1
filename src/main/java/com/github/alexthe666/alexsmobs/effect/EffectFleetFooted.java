package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectFleetFooted extends MobEffect {

    private static final AttributeModifier SPRINT_JUMP_SPEED_BONUS = new AttributeModifier(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("alexsmobs", "fleetfooted_speed_bonus"), 0.2F, AttributeModifier.Operation.ADD_VALUE);
    private int removeEffectAfter = 0;

    public EffectFleetFooted() {
        super(MobEffectCategory.BENEFICIAL, 0X685441);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Get the effect instance to check duration
        var effectInstance = entity.getEffect(AMEffectRegistry.FLEET_FOOTED);
        int currentDuration = effectInstance != null ? effectInstance.getDuration() : 0;
        
        AttributeInstance modifiableattributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        boolean applyEffect = entity.isSprinting() && !entity.onGround() && currentDuration > 2;
        if(removeEffectAfter > 0){
            removeEffectAfter--;
        }
        if (applyEffect) {
            if(!modifiableattributeinstance.hasModifier(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("alexsmobs", "fleetfooted_speed_bonus"))){
                modifiableattributeinstance.addPermanentModifier(SPRINT_JUMP_SPEED_BONUS);
            }
            removeEffectAfter = 5;
        }
        if (removeEffectAfter <= 0 || currentDuration < 2) {
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
        }
        return true;
    }

    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int level) {
        AttributeInstance modifiableattributeinstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if(modifiableattributeinstance != null && modifiableattributeinstance.hasModifier(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("alexsmobs", "fleetfooted_speed_bonus"))){
            modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_BONUS);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.fleet_footed";
    }

}