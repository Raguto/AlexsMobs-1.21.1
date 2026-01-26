package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityVineLasso;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemVineLasso extends Item {

    public ItemVineLasso(Properties props) {
        super(props);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public static boolean isItemInUse(ItemStack stack){
        return true; // Lasso is always usable
    }

    public void inventoryTick(ItemStack stack, Level world, Entity entity, int i, boolean b) {
        // No longer need NBT tracking - use duration handles this
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);

        return InteractionResultHolder.consume(itemstack);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if(count % 7 == 0){
            livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_START);
            livingEntityIn.playSound(AMSoundRegistry.VINE_LASSO.get(),1.0F, 1.0F + (livingEntityIn.getRandom().nextFloat() - livingEntityIn.getRandom().nextFloat()) * 0.2F);
        }
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity livingEntityIn, int i) {
        if (!worldIn.isClientSide) {
            int power = this.getUseDuration(stack, livingEntityIn) - i;
            float strength = getPowerForTime(power);
            if (livingEntityIn instanceof Player player) {
                HitResult hitResult = player.pick(15.0D, 1.0F, false);
                if (hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHit = (EntityHitResult) hitResult;
                    if (entityHit.getEntity() instanceof LivingEntity target && target != player) {
                        VineLassoUtil.lassoTo(player, target);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        return;
                    }
                }
            }
            EntityVineLasso lasso = new EntityVineLasso(worldIn, livingEntityIn);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            lasso.shoot((double) vector3d.x(), (double) vector3d.y(), (double) vector3d.z(), Math.max(0.1F, strength), 1);
            worldIn.addFreshEntity(lasso);
            if (livingEntityIn instanceof Player player && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
    }

    public static float getPowerForTime(int p) {
        float f = (float)p / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }


    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }
}
