package com.github.alexthe666.alexsmobs.entity;

import net.neoforged.neoforge.common.ItemAbilities;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.Tool;
// NetworkHooks removed in NeoForge 1.21

public class EntitySharkToothArrow extends Arrow {

    public EntitySharkToothArrow(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public EntitySharkToothArrow(EntityType type, double x, double y, double z, Level worldIn) {
        this(type, worldIn);
        this.setPos(x, y, z);
    }

    public EntitySharkToothArrow(Level worldIn, LivingEntity shooter) {
        this(AMEntityRegistry.SHARK_TOOTH_ARROW.get(), shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), worldIn);
        this.setOwner(shooter);
        if (shooter instanceof Player) {
            this.pickup = AbstractArrow.Pickup.ALLOWED;
        }
    }

    protected void damageShield(Player player, float damage) {
        if (damage >= 3.0F && player.getUseItem().getItem().canPerformAction(player.getUseItem(), ItemAbilities.SHIELD_BLOCK)) {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, EquipmentSlot.MAINHAND);

            if (player.getUseItem().isEmpty()) {
                InteractionHand Hand = player.getUsedItemHand();
                net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(player, copyBeforeUse, Hand);

                if (Hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
                    // Equipment slot removed
                } else {
                    // Equipment slot removed
                }
                player.stopUsingItem();
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }
        }
    }

    protected void doPostHurtEffects(LivingEntity living) {
        if (living instanceof Player) {
            this.damageShield((Player) living, (float) this.getBaseDamage());
        }
        Entity entity1 = this.getOwner();
        if(living.getType().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD) /* TODO: verify tag */ || living instanceof Drowned || !living.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD) && living.canBreatheUnderwater()){
            DamageSource damagesource;
            if (entity1 == null) {
                damagesource = damageSources().arrow(this, this);
            } else {
                damagesource = damageSources().arrow(this, entity1);
            }
            living.hurt(damagesource, 7);
        }
    }


    public boolean isInWater() {
        return false;
    }
    // getAddEntityPacket is no longer needed in 1.21
    // public Packet<ClientGamePacketListener> getAddEntityPacket() {


    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(AMItemRegistry.SHARK_TOOTH_ARROW.get());
    }

}
