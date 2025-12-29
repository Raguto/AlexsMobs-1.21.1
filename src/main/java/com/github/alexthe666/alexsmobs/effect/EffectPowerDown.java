package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.gameevent.GameEvent;

public class EffectPowerDown extends MobEffect {

    private int lastDuration = -1;
    private int firstDuration = -1;

    protected EffectPowerDown() {
        super(MobEffectCategory.NEUTRAL, 0x00000);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.parse("alexsmobs:power_down_speed"), (double)-1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.getDeltaMovement().y > 0 && !entity.isInWaterOrBubble()){
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
        }
        if(firstDuration == lastDuration){
            entity.playSound(AMSoundRegistry.APRIL_FOOLS_POWER_OUTAGE.get(), 1.5F, 1);
            entity.gameEvent(GameEvent.ENTITY_ACTION);
        }
        return true;
    }

    public int getActiveTime(){
        return firstDuration - lastDuration;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        if(duration <= 0){
            lastDuration = -1;
            firstDuration = -1;
        }
        if(firstDuration == -1){
            firstDuration = duration;
        }
        return duration > 0;
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.removeAttributeModifiers(map);
    }

    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.addAttributeModifiers(map, i);
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.power_down";
    }
}
