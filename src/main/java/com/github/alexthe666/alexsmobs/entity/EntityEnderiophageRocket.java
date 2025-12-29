package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.OptionalInt;

/**
 * Custom rocket entity for the Enderiophage Rocket item.
 * Reimplemented to avoid accessing private fields in FireworkRocketEntity.
 */
public class EntityEnderiophageRocket extends Projectile implements ItemSupplier {

    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(EntityEnderiophageRocket.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    
    private int life;
    private int lifetime;

    public EntityEnderiophageRocket(EntityType<? extends EntityEnderiophageRocket> type, Level level) {
        super(type, level);
        this.lifetime = 18 + this.random.nextInt(14);
    }

    public EntityEnderiophageRocket(Level worldIn, double x, double y, double z, ItemStack givenItem) {
        this(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.lifetime = 18 + this.random.nextInt(14);
    }

    public EntityEnderiophageRocket(Level level, @Nullable Entity owner, double x, double y, double z, ItemStack stack) {
        this(level, x, y, z, stack);
        this.setOwner(owner);
    }

    public EntityEnderiophageRocket(Level level, ItemStack stack, LivingEntity shooter) {
        this(level, shooter, shooter.getX(), shooter.getY(), shooter.getZ(), stack);
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(shooter.getId()));
        this.lifetime = 18 + this.random.nextInt(14);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return super.shouldRender(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        
        // Handle attached entity (elytra boost)
        if (this.isAttachedToEntity()) {
            Entity attached = this.getAttachedEntity();
            if (attached != null) {
                if (attached instanceof LivingEntity living && living.isFallFlying()) {
                    Vec3 lookVec = attached.getLookAngle();
                    Vec3 deltaMovement = attached.getDeltaMovement();
                    attached.setDeltaMovement(deltaMovement.add(
                        lookVec.x * 0.1D + (lookVec.x * 1.5D - deltaMovement.x) * 0.5D,
                        lookVec.y * 0.1D + (lookVec.y * 1.5D - deltaMovement.y) * 0.5D,
                        lookVec.z * 0.1D + (lookVec.z * 1.5D - deltaMovement.z) * 0.5D
                    ));
                }
                this.setPos(attached.getX(), attached.getY(), attached.getZ());
                this.setDeltaMovement(attached.getDeltaMovement());
            }
        } else {
            // Normal projectile movement
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.15D, 1.0D, 1.15D).add(0.0D, 0.04D, 0.0D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        // Check for collision
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!this.noPhysics) {
            this.onHit(hitresult);
            this.hasImpulse = true;
        }

        // Spawn particles
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY() - 0.3D, this.getZ(), 
                this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        // Check lifetime
        ++this.life;
        if (!this.level().isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    private boolean isAttachedToEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent();
    }

    @Nullable
    private Entity getAttachedEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent() 
            ? this.level().getEntity(this.entityData.get(DATA_ATTACHED_TO_TARGET).getAsInt()) 
            : null;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (result.getType() == HitResult.Type.BLOCK && !this.level().isClientSide) {
            this.explode();
        }
    }

    private void explode() {
        this.level().broadcastEntityEvent(this, (byte) 17);
        this.discard();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 17) {
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 
                this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
            for (int i = 0; i < this.random.nextInt(15) + 30; ++i) {
                this.level().addParticle(AMParticleRegistry.DNA.get(), this.getX(), this.getY(), this.getZ(), 
                    this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D);
            }
            for (int i = 0; i < this.random.nextInt(15) + 15; ++i) {
                this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 
                    this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D);
            }
            SoundEvent soundEvent = AlexsMobs.PROXY.isFarFromCamera(this.getX(), this.getY(), this.getZ()) 
                ? SoundEvents.FIREWORK_ROCKET_BLAST : SoundEvents.FIREWORK_ROCKET_BLAST_FAR;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", this.life);
        tag.putInt("LifeTime", this.lifetime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.life = tag.getInt("Life");
        this.lifetime = tag.getInt("LifeTime");
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(AMItemRegistry.ENDERIOPHAGE_ROCKET.get());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
