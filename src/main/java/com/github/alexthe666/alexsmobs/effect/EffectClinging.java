package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.block.state.BlockState;

public class EffectClinging extends MobEffect {

    public EffectClinging() {
        super(MobEffectCategory.BENEFICIAL, 0XBD4B4B);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.refreshDimensions();
        entity.setNoGravity(false);

        boolean nearCeiling = isNearCeiling(entity);
        boolean touchingCeiling = entity.verticalCollision && !entity.onGround();
        double yVelocity = entity.getDeltaMovement().y;
        boolean shouldActivate = touchingCeiling || (nearCeiling && !entity.onGround() && yVelocity > 0);
        
        if (shouldActivate) {
            entity.fallDistance = 0;
            if (!entity.isShiftKeyDown()) {
                if (touchingCeiling) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, 0.08, entity.getDeltaMovement().z);
                } else {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.15F, 0));
                }
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.98F, 1F, 0.98F));
            } else {
                if (touchingCeiling) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, -0.2, entity.getDeltaMovement().z);
                }
            }
        }
        return true;
    }

    public static boolean isNearCeiling(LivingEntity entity) {
        for (double offset = 0.1; offset <= 3.0; offset += 0.3) {
            BlockPos pos = AMBlockPos.fromCoords(entity.getX(), entity.getBoundingBox().maxY + offset, entity.getZ());
            BlockState state = entity.level().getBlockState(pos);
            if (!state.isAir() && !state.getCollisionShape(entity.level(), pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUpsideDown(LivingEntity entity){
        return entity.verticalCollision && !entity.onGround() && isNearCeiling(entity);
    }
    
    public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        super.removeAttributeModifiers(attributeMapIn);
        entityLivingBaseIn.refreshDimensions();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.clinging";
    }

}