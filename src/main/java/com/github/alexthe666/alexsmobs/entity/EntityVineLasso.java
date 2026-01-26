package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
// NetworkHooks removed in NeoForge 1.21

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityVineLasso extends Entity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID_DATA = SynchedEntityData.defineId(EntityVineLasso.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> OWNER_ID_DATA = SynchedEntityData.defineId(EntityVineLasso.class, EntityDataSerializers.INT);

    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    public EntityVineLasso(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityVineLasso(Level worldIn, LivingEntity entity) {
        this(AMEntityRegistry.VINE_LASSO.get(), worldIn);
        this.setShooter(entity);
        this.setPos(entity.getX(), entity.getEyeY() + (double)0.15F, entity.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    public EntityVineLasso(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.VINE_LASSO.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }
    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while (p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while (p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }

    // getAddEntityPacket is no longer needed in 1.21
    // public Packet<ClientGamePacketListener> getAddEntityPacket() {

    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            if (!this.level().isClientSide) {
                this.onImpact(raytraceresult);
            }
        }

        this.updateRotation();
        if(this.getOwner() != null && this.distanceTo(this.getOwner()) > 15){
            this.removeAndAddToInventory();
        }
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir) && !this.isInWater() && !this.isInLava()) {
            this.removeAndAddToInventory();
        }else {
            final double d0 = this.getX() + vector3d.x;
            final double d1 = this.getY() + vector3d.y;
            final double d2 = this.getZ() + vector3d.z;
            this.setDeltaMovement(vector3d.scale(0.99F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.02F, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
    }

    protected void onEntityHit(EntityHitResult p_213868_1_) {
        Entity ownerEntity = this.getOwner();
        LivingEntity lassoer = ownerEntity instanceof LivingEntity ? (LivingEntity) ownerEntity : null;
        if (lassoer == null && !this.level().isClientSide) {
            UUID owner = this.getOwnerUUID();
            if (owner != null && ServerLifecycleHooks.getCurrentServer() != null) {
                lassoer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(owner);
            }
            if (lassoer == null) {
                lassoer = this.level().getNearestPlayer(this, 64.0D);
            }
        }
        if (lassoer != null && p_213868_1_.getEntity() != lassoer && p_213868_1_.getEntity() instanceof LivingEntity) {
            VineLassoUtil.lassoTo(lassoer, (LivingEntity)p_213868_1_.getEntity());
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private void removeAndAddToInventory(){
        Entity entity = this.getOwner();
        ItemStack item = new ItemStack(AMItemRegistry.VINE_LASSO.get());
        if(!this.isRemoved()){
            if (!(entity instanceof Player) || !((Player) entity).addItem(item)) {
                this.spawnAtLocation(item);
            }
        }
        this.remove(RemovalReason.DISCARDED);
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level().getBlockState(p_230299_1_.getBlockPos());
        if (!this.level().isClientSide) {
            this.removeAndAddToInventory();
        }
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_UUID_DATA, Optional.empty());
        builder.define(OWNER_ID_DATA, 0);
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
            this.entityData.set(OWNER_UUID_DATA, Optional.of(this.ownerUUID));
            this.entityData.set(OWNER_ID_DATA, this.ownerNetworkId);
        }
    }

    @Nullable
    private UUID getOwnerUUID() {
        if (this.ownerUUID != null) {
            return this.ownerUUID;
        }
        Optional<UUID> synced = this.entityData.get(OWNER_UUID_DATA);
        if (synced != null && synced.isPresent()) {
            this.ownerUUID = synced.get();
            return this.ownerUUID;
        }
        return null;
    }

    private int getOwnerNetworkId() {
        if (this.ownerNetworkId != 0) {
            return this.ownerNetworkId;
        }
        int syncedId = this.entityData.get(OWNER_ID_DATA);
        if (syncedId != 0) {
            this.ownerNetworkId = syncedId;
        }
        return this.ownerNetworkId;
    }

    @Nullable
    public Entity getOwner() {
        UUID uuid = this.getOwnerUUID();
        int networkId = this.getOwnerNetworkId();
        if (uuid != null && this.level() instanceof ServerLevel) {
            Entity found = ((ServerLevel) this.level()).getEntity(uuid);
            if (found != null) {
                return found;
            }
            return networkId != 0 ? this.level().getEntity(networkId) : null;
        } else {
            return networkId != 0 ? this.level().getEntity(networkId) : null;
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            compound.putUUID("Owner", uuid);
        }

        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.entityData.set(OWNER_UUID_DATA, Optional.of(this.ownerUUID));
        } else {
            this.ownerUUID = null;
            this.entityData.set(OWNER_UUID_DATA, Optional.empty());
        }

        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for (Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale(velocity);
        this.setDeltaMovement(vector3d);
        final float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setYRot( (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        final float f = -Mth.sin(p_234612_3_ * Mth.DEG_TO_RAD) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        final float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * Mth.DEG_TO_RAD);
        final float f2 = Mth.cos(p_234612_3_ * Mth.DEG_TO_RAD) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) result);
        } else if (raytraceresult$type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float)(x * x + z * z));
            this.setXRot((float) (Mth.atan2(y, f) * (double) Mth.RAD_TO_DEG));
            this.setYRot( (float) (Mth.atan2(x, z) * (double) Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getOwner();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG)));
        this.setYRot(this.getYRot() + 20);
    }
}
