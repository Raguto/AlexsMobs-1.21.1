package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@EventBusSubscriber(modid = AlexsMobs.MODID)
public class ServerEvents {

    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
    private static final ResourceLocation SAND_SPEED_ID = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "roadrunner_sand_speed");
    private static final ResourceLocation SNEAK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, "frontier_cap_sneak_speed");
    private static final Random RAND = new Random();

    protected static BlockHitResult rayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        float x = player.getXRot();
        float y = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        float f0 = -y * Mth.DEG_TO_RAD - Mth.PI;
        float f1 = -x * Mth.DEG_TO_RAD;
        float f2 = Mth.cos(f0);
        float f3 = Mth.sin(f0);
        float f4 = -Mth.cos(f1);
        float f5 = Mth.sin(f1);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.blockInteractionRange();
        Vec3 vector3d1 = vector3d.add(f6 * d0, f5 * d0, f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    /**
     * Check if an entity should not scare pufferfish (Alex's Mobs fish and other aquatic creatures)
     */
    private static boolean isNotScaryForPufferfish(LivingEntity entity) {
        return entity instanceof EntityLobster ||
               entity instanceof EntityBlobfish ||
               entity instanceof EntityTerrapin ||
               entity instanceof EntityCombJelly ||
               entity instanceof EntityCosmicCod ||
               entity instanceof EntityCatfish ||
               entity instanceof EntityFlyingFish ||
               entity instanceof EntityMudskipper ||
               entity instanceof EntityTriops ||
               entity instanceof EntityDevilsHolePupfish ||
               entity instanceof AbstractFish ||  // Vanilla fish
               entity instanceof AbstractSchoolingFish ||  // Vanilla schooling fish
               entity instanceof Squid ||  // Vanilla squid
               entity instanceof Dolphin;  // Vanilla dolphin
    }

    private static net.minecraft.network.syncher.EntityDataAccessor<Integer> PUFF_STATE_ACCESSOR = null;
    private static boolean PUFF_STATE_INIT_ATTEMPTED = false;
    
    /**
     * Set pufferfish puff state using reflection
     */
    @SuppressWarnings("unchecked")
    private static void setPufferfishState(Pufferfish pufferfish, int state) {
        if (!PUFF_STATE_INIT_ATTEMPTED) {
            PUFF_STATE_INIT_ATTEMPTED = true;
            try {
                // Find the PUFF_STATE field by scanning all static fields
                for (var field : Pufferfish.class.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                        net.minecraft.network.syncher.EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        var accessor = (net.minecraft.network.syncher.EntityDataAccessor<?>) field.get(null);
                        // Test if this is the puff state accessor by checking current value type
                        try {
                            Object value = pufferfish.getEntityData().get(accessor);
                            if (value instanceof Integer && field.getName().toLowerCase().contains("puff")) {
                                PUFF_STATE_ACCESSOR = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) accessor;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }
                // If not found by name, try to find by matching the getter value
                if (PUFF_STATE_ACCESSOR == null) {
                    int currentPuffState = pufferfish.getPuffState();
                    for (var field : Pufferfish.class.getDeclaredFields()) {
                        if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && 
                            net.minecraft.network.syncher.EntityDataAccessor.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            var accessor = (net.minecraft.network.syncher.EntityDataAccessor<?>) field.get(null);
                            try {
                                Object value = pufferfish.getEntityData().get(accessor);
                                if (value instanceof Integer intVal && intVal == currentPuffState) {
                                    PUFF_STATE_ACCESSOR = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) accessor;
                                    break;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Failed to find PUFF_STATE field: " + e.getMessage());
            }
        }
        
        if (PUFF_STATE_ACCESSOR != null) {
            pufferfish.getEntityData().set(PUFF_STATE_ACCESSOR, state);
        }
    }

    private static java.lang.reflect.Field INFLATE_COUNTER_FIELD = null;
    private static java.lang.reflect.Field DEFLATE_TIMER_FIELD = null;
    private static boolean INFLATE_FIELDS_INIT_ATTEMPTED = false;
    
    /**
     * Reset pufferfish inflate counter to prevent re-inflation
     */
    private static void resetPufferfishInflateCounter(Pufferfish pufferfish) {
        if (!INFLATE_FIELDS_INIT_ATTEMPTED) {
            INFLATE_FIELDS_INIT_ATTEMPTED = true;
            try {
                // Find inflateCounter and deflateTimer fields
                for (var field : Pufferfish.class.getDeclaredFields()) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                        String name = field.getName().toLowerCase();
                        if (name.contains("inflate") || name.contains("counter")) {
                            field.setAccessible(true);
                            INFLATE_COUNTER_FIELD = field;
                        } else if (name.contains("deflate") || name.contains("timer")) {
                            field.setAccessible(true);
                            DEFLATE_TIMER_FIELD = field;
                        }
                    }
                }
                // If not found by name, get all int fields
                if (INFLATE_COUNTER_FIELD == null) {
                    for (var field : Pufferfish.class.getDeclaredFields()) {
                        if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                            field.setAccessible(true);
                            // The first non-static int field is likely inflateCounter
                            if (INFLATE_COUNTER_FIELD == null) {
                                INFLATE_COUNTER_FIELD = field;
                            } else if (DEFLATE_TIMER_FIELD == null) {
                                DEFLATE_TIMER_FIELD = field;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Failed to find inflate counter fields: " + e.getMessage());
            }
        }
        
        try {
            if (INFLATE_COUNTER_FIELD != null) {
                INFLATE_COUNTER_FIELD.setInt(pufferfish, 0);
            }
            if (DEFLATE_TIMER_FIELD != null) {
                DEFLATE_TIMER_FIELD.setInt(pufferfish, 0);
            }
        } catch (Exception ignored) {}
    }

    private static BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
        int i = 0;
        while (world.isEmptyBlock(entered) && i < 3) {
            entered = entered.below();
            i++;
        }
        return entered;
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AMConfig.giveBookOnStartup) {
            CompoundTag playerData = event.getEntity().getPersistentData();
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("alexsmobs_has_book")) {
                ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY.get()));
                boolean isAlex = Objects.equals(event.getEntity().getUUID(), ALEX_UUID);
                if (isAlex || Objects.equals(event.getEntity().getUUID(), CARRO_UUID)) {
                    ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.BEAR_DUST.get()));
                }
                if (isAlex) {
                    ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.NOVELTY_HAT.get()));
                }
                data.putBoolean("alexsmobs_has_book", true);
                playerData.put(Player.PERSISTED_NBT_TAG, data);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttackEntityEvent(AttackEntityEvent event) {
        if (event.getTarget() instanceof LivingEntity living) {
            if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR.get()) {
                living.knockback(1F, Mth.sin(event.getEntity().getYRot() * Mth.DEG_TO_RAD),
                        -Mth.cos(event.getEntity().getYRot() * Mth.DEG_TO_RAD));
            }
            if (event.getEntity().hasEffect(AMEffectRegistry.TIGERS_BLESSING)
                    && !event.getTarget().isAlliedTo(event.getEntity()) && !(event.getTarget() instanceof EntityTiger)) {
                AABB bb = new AABB(event.getEntity().getX() - 32, event.getEntity().getY() - 32, event.getEntity().getZ() - 32,
                        event.getEntity().getX() + 32, event.getEntity().getY() + 32, event.getEntity().getZ() + 32);
                var tigers = event.getEntity().level().getEntitiesOfClass(EntityTiger.class, bb, EntitySelector.ENTITY_STILL_ALIVE);
                for (EntityTiger tiger : tigers) {
                    if (!tiger.isBaby()) {
                        tiger.setTarget(living);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.getEntity() != null && event.getEntity().isHolding(AMItemRegistry.GHOSTLY_PICKAXE.get())
                && ItemGhostlyPickaxe.shouldStoreInGhost(event.getEntity(), event.getEntity().getMainHandItem())) {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity().getType() == EntityType.SQUID && !event.getEntity().level().isClientSide) {
            ServerLevel level = (ServerLevel) event.getEntity().level();
            event.setCanceled(true);
            EntityGiantSquid squid = AMEntityRegistry.GIANT_SQUID.get().create(level);
            squid.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                    event.getEntity().getYRot(), event.getEntity().getXRot());
            squid.finalizeSpawn(level, level.getCurrentDifficultyAt(squid.blockPosition()), MobSpawnType.CONVERSION, null);
            if (event.getEntity().hasCustomName()) {
                squid.setCustomName(event.getEntity().getCustomName());
                squid.setCustomNameVisible(event.getEntity().isCustomNameVisible());
            }
            squid.setBlue(true);
            squid.setPersistenceRequired();
            level.addFreshEntityWithPassengers(squid);
            event.getEntity().discard();
        }
    }


    @SubscribeEvent
    public static void onProjectileHit(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult hitResult
                && hitResult.getEntity() instanceof EntityEmu emu && !event.getEntity().level().isClientSide) {
            if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT)
                    && emu.getAnimationTick() < 7) {
                event.setCanceled(true);
                return;
            }
            if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
                boolean left;
                Vec3 arrowPos = event.getEntity().position();
                Vec3 rightVector = emu.getLookAngle().yRot(0.5F * Mth.PI).add(emu.position());
                Vec3 leftVector = emu.getLookAngle().yRot(-0.5F * Mth.PI).add(emu.position());
                if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
                    left = false;
                } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
                    left = true;
                } else {
                    left = emu.getRandom().nextBoolean();
                }
                Vec3 vector3d2 = event.getEntity().getDeltaMovement().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
                emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
                emu.hasImpulse = true;
                if (!emu.horizontalCollision) {
                    emu.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.25F, 0.1F, vector3d2.z() * 0.25F));
                }
                if (event.getEntity() instanceof Projectile projectile) {
                    if (projectile.getOwner() instanceof ServerPlayer serverPlayer) {
                        AlexsMobs.LOGGER.info("Emu dodged! Triggering EMU_DODGE advancement for player: {}", serverPlayer.getName().getString());
                        AMAdvancementTriggerRegistry.EMU_DODGE.get().trigger(serverPlayer);
                    }
                }
                emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5F, 0.32F, vector3d2.z() * 0.5F));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        var entity = event.getEntity();
        try {
            if (AMConfig.spidersAttackFlies && entity instanceof Spider spider) {
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 1, true, false, null));
            } else if (AMConfig.wolvesAttackMoose && entity instanceof Wolf wolf) {
                wolf.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(wolf, EntityMoose.class, false, null));
            } else if (AMConfig.polarBearsAttackSeals && entity instanceof PolarBear bear) {
                bear.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(bear, EntitySeal.class, 15, true, true, null));
            } else if (entity instanceof Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntityTiger.class, 6.0F, 1.0D, 1.2D));
            } else if (AMConfig.catsAndFoxesAttackJerboas && (entity instanceof Fox || entity instanceof Cat || entity instanceof Ocelot)) {
                Mob mb = (Mob) entity;
                mb.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(mb, EntityJerboa.class, 45, true, true, null));
            } else if (AMConfig.bunfungusTransformation && entity instanceof Rabbit rabbit) {
                rabbit.goalSelector.addGoal(3, new TemptGoal(rabbit, 1.0D, Ingredient.of(AMItemRegistry.MUNGAL_SPORES.get()), false));
            } else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof Dolphin dolphin) {
                dolphin.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(dolphin, EntityFlyingFish.class, 70, true, true, null));
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        if (VineLassoUtil.hasLassoData(event.getEntity())) {
            VineLassoUtil.lassoTo(null, event.getEntity());
            event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(),
                    event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(AMItemRegistry.VINE_LASSO.get())));
        }
    }

    @SubscribeEvent
    public static void onItemUseLast(LivingEntityUseItemEvent.Finish event) {
        if (event.getItem().getItem() == Items.CHORUS_FRUIT && RAND.nextInt(3) == 0
                && event.getEntity().hasEffect(AMEffectRegistry.ENDER_FLU)) {
            event.getEntity().removeEffect(AMEffectRegistry.ENDER_FLU);
        }
    }


    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        var entity = (LivingEntity) event.getEntity();
        // Make pufferfish not puff up around Alex's Mobs fish
        if (entity instanceof Pufferfish pufferfish && !entity.level().isClientSide) {
            // Check if only Alex's Mobs fish are nearby (no scary entities)
            var nearbyEntities = pufferfish.level().getEntitiesOfClass(LivingEntity.class, 
                pufferfish.getBoundingBox().inflate(2.0D), 
                e -> e != pufferfish && !(e instanceof Pufferfish) && !e.isSpectator());
            
            boolean hasScaryEntity = false;
            boolean hasFriendlyFish = false;
            for (LivingEntity nearby : nearbyEntities) {
                if (isNotScaryForPufferfish(nearby)) {
                    hasFriendlyFish = true;
                } else {
                    hasScaryEntity = true;
                    break;
                }
            }
            // If only friendly fish nearby (no scary entities), keep pufferfish deflated
            if (hasFriendlyFish && !hasScaryEntity) {
                setPufferfishState(pufferfish, 0);
                resetPufferfishInflateCounter(pufferfish);
            }
        }
        
        if (entity instanceof Player player) {
            if (entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                var attributes = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS.get()
                        || attributes.hasModifier(SAND_SPEED_ID)) {
                    boolean sand = player.level().getBlockState(getDownPos(player.blockPosition(), player.level())).is(BlockTags.SAND);
                    if (sand && !attributes.hasModifier(SAND_SPEED_ID)) {
                        attributes.addPermanentModifier(new AttributeModifier(SAND_SPEED_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE));
                    }
                    if (player.tickCount % 25 == 0
                            && (player.getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS.get() || !sand)
                            && attributes.hasModifier(SAND_SPEED_ID)) {
                        attributes.removeModifier(SAND_SPEED_ID);
                    }
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP.get()
                        || attributes.hasModifier(SNEAK_SPEED_ID)) {
                    boolean shift = player.isShiftKeyDown();
                    if (shift && !attributes.hasModifier(SNEAK_SPEED_ID)) {
                        attributes.addPermanentModifier(new AttributeModifier(SNEAK_SPEED_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE));
                    }
                    if ((!shift || player.getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP.get())
                            && attributes.hasModifier(SNEAK_SPEED_ID)) {
                        attributes.removeModifier(SNEAK_SPEED_ID);
                    }
                }
            }
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                if (!player.isEyeInFluid(FluidTags.WATER)) {
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 310, 0, false, false, true));
                }
            }
        }
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty()) {
            CompoundTag tag = boots.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            if (tag.contains("BisonFur") && tag.getBoolean("BisonFur")) {
                BlockPos posBelow = new BlockPos((int) entity.getX(), (int) (entity.getBoundingBox().minY - 0.1F), (int) entity.getZ());
                if (entity.level().getBlockState(posBelow).is(Blocks.POWDER_SNOW)) {
                    entity.setOnGround(true);
                    entity.setTicksFrozen(0);
                    entity.setPos(entity.getX(), Math.max(entity.getY(), posBelow.getY() + 1F), entity.getZ());
                }
                if (entity.isInPowderSnow) {
                    entity.setOnGround(true);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1F, 0));
                }
            }
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS.get()) {
            if (entity.horizontalCollision && !entity.isInWater()) {
                entity.fallDistance = 0.0F;
                Vec3 motion = entity.getDeltaMovement();
                double d2 = 0.1D;
                if (entity.isShiftKeyDown() || entity.isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(Mth.clamp(motion.x, -0.15F, 0.15F), d2, Mth.clamp(motion.z, -0.15F, 0.15F));
                entity.setDeltaMovement(motion);
            }
        }

        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO.get()
                && !entity.level().isClientSide && AlexsMobs.isAprilFools() && entity.isInWaterOrBubble()) {
            RandomSource random = entity.getRandom();
            if (random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
                int dist = 32;
                var nearbySeabears = entity.level().getEntitiesOfClass(EntitySeaBear.class, entity.getBoundingBox().inflate(dist, dist, dist));
                if (nearbySeabears.isEmpty()) {
                    EntitySeaBear bear = AMEntityRegistry.SEA_BEAR.get().create(entity.level());
                    BlockPos at = entity.blockPosition();
                    BlockPos farOff = null;
                    for (int i = 0; i < 15; i++) {
                        int f1 = (int) Math.signum(random.nextInt() - 0.5F);
                        int f2 = (int) Math.signum(random.nextInt() - 0.5F);
                        BlockPos pos1 = at.offset(f1 * (10 + random.nextInt(dist - 10)), random.nextInt(1), f2 * (10 + random.nextInt(dist - 10)));
                        if (entity.level().isWaterAt(pos1)) {
                            farOff = pos1;
                        }
                    }
                    if (farOff != null) {
                        bear.setPos(farOff.getX() + 0.5F, farOff.getY() + 0.5F, farOff.getZ() + 0.5F);
                        bear.setYRot(random.nextFloat() * 360F);
                        bear.setTarget(entity);
                        entity.level().addFreshEntity(bear);
                    }
                } else {
                    for (EntitySeaBear seaBear : nearbySeabears) {
                        seaBear.setTarget(entity);
                    }
                }
            }
        }
        if (VineLassoUtil.hasLassoData(entity)) {
            VineLassoUtil.tickLasso(entity);
        }
        if (RockyChestplateUtil.isWearing(entity)) {
            RockyChestplateUtil.tickRockyRolling(entity);
        }
        if (FlyingFishBootsUtil.isWearing(entity)) {
            FlyingFishBootsUtil.tickFlyingFishBoots(entity);
        }
    }

    @SubscribeEvent
    public static void onLivingSetTargetEvent(LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() != null && event.getEntity() instanceof Mob mob) {
            // Check for arthropod type using vanilla tag
            if (mob.getType().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD)) {
                if (event.getNewAboutToBeSetTarget().hasEffect(AMEffectRegistry.BUG_PHEROMONES)
                        && event.getEntity().getLastHurtByMob() != event.getNewAboutToBeSetTarget()) {
                    event.setCanceled(true);
                    return;
                }
            }
            // Check for undead type using vanilla tag
            if (mob.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD) && !mob.getType().is(AMTagRegistry.IGNORES_KIMONO)) {
                if (event.getNewAboutToBeSetTarget().getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get())
                        && event.getEntity().getLastHurtByMob() != event.getNewAboutToBeSetTarget()) {
                    event.setCanceled(true);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (event.getNewDamage() > 0 && attacker.hasEffect(AMEffectRegistry.SOULSTEAL)
                    && attacker.getEffect(AMEffectRegistry.SOULSTEAL) != null) {
                int level = attacker.getEffect(AMEffectRegistry.SOULSTEAL).getAmplifier() + 1;
                if (attacker.getHealth() < attacker.getMaxHealth()
                        && ThreadLocalRandom.current().nextFloat() < (0.25F + (level * 0.25F))) {
                    attacker.heal(Math.min(event.getNewDamage() / 2F * level, 2 + 2 * level));
                }
            }
            if (event.getEntity() instanceof Player player) {
                if (attacker instanceof EntityMimicOctopus octopus && octopus.isOwnedBy(player)) {
                    event.setNewDamage(0);
                    return;
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                    if (attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F) {
                        attacker.hurt(attacker.damageSources().thorns(player), 1F);
                        attacker.knockback(0.5F, Mth.sin((attacker.getYRot() + 180) * Mth.DEG_TO_RAD),
                                -Mth.cos((attacker.getYRot() + 180) * Mth.DEG_TO_RAD));
                    }
                }
            }
        }
        if (!event.getEntity().getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                && event.getEntity().getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS.get()) {
            if (event.getSource().is(DamageTypeTags.IS_PROJECTILE) && event.getEntity().getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                event.setNewDamage(0);
            }
        }
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (event.getItemStack().getItem() == Items.WHEAT && player.getVehicle() instanceof EntityElephant elephant) {
            if (elephant.triggerCharge(event.getItemStack())) {
                player.swing(event.getHand());
                if (!player.isCreative()) {
                    event.getItemStack().shrink(1);
                }
            }
        }
        if (event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
            HitResult raytraceresult = rayTrace(event.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (event.getLevel().mayInteract(player, blockpos)) {
                    if (event.getLevel().getFluidState(blockpos).is(FluidTags.LAVA)) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        event.getLevel().playSound(player, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        player.igniteForSeconds(6);
                        if (!player.addItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()))) {
                            player.spawnAtLocation(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()));
                        }
                        player.swing(event.getHand());
                        if (!player.isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity living) {
            if (!event.getEntity().isShiftKeyDown() && VineLassoUtil.hasLassoData(living)) {
                if (!event.getEntity().level().isClientSide) {
                    event.getTarget().spawnAtLocation(new ItemStack(AMItemRegistry.VINE_LASSO.get()));
                }
                VineLassoUtil.lassoTo(null, living);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
            if (!(event.getTarget() instanceof Player) && !(event.getTarget() instanceof EntityEndergrade)
                    && living.hasEffect(AMEffectRegistry.ENDER_FLU)) {
                if (event.getItemStack().getItem() == Items.CHORUS_FRUIT) {
                    if (!event.getEntity().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                    event.getTarget().gameEvent(GameEvent.EAT);
                    event.getTarget().playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F + event.getEntity().getRandom().nextFloat());
                    if (event.getEntity().getRandom().nextFloat() < 0.4F) {
                        living.removeEffect(AMEffectRegistry.ENDER_FLU);
                        Items.CHORUS_FRUIT.finishUsingItem(event.getItemStack().copy(), event.getLevel(), living);
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
            if (RainbowUtil.getRainbowType(living) > 0 && event.getItemStack().getItem() == Items.SPONGE) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                RainbowUtil.setRainbowType(living, 0);
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
                if (!event.getEntity().addItem(wetSponge)) {
                    event.getEntity().drop(wetSponge, true);
                }
            }
            if (living instanceof Rabbit rabbit && event.getItemStack().getItem() == AMItemRegistry.MUNGAL_SPORES.get()
                    && AMConfig.bunfungusTransformation) {
                var random = ThreadLocalRandom.current();
                if (!event.getEntity().level().isClientSide && random.nextFloat() < 0.15F) {
                    EntityBunfungus bunfungus = rabbit.convertTo(AMEntityRegistry.BUNFUNGUS.get(), true);
                    if (bunfungus != null) {
                        event.getEntity().level().addFreshEntity(bunfungus);
                        bunfungus.setTransformsIn(EntityBunfungus.MAX_TRANSFORM_TIME);
                    }
                } else {
                    for (int i = 0; i < 2 + random.nextInt(2); i++) {
                        double d0 = random.nextGaussian() * 0.02D;
                        double d1 = 0.05F + random.nextGaussian() * 0.02D;
                        double d2 = random.nextGaussian() * 0.02D;
                        event.getTarget().level().addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(),
                                event.getTarget().getRandomX(0.7F), event.getTarget().getY(0.6F),
                                event.getTarget().getRandomZ(0.7F), d0, d1, d2);
                    }
                }
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }


    @SubscribeEvent
    public static void onUseItemAir(PlayerInteractEvent.RightClickEmpty event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (stack.isEmpty()) {
            stack = event.getEntity().getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (RainbowUtil.getRainbowType(event.getEntity()) > 0 && stack.is(Items.SPONGE)) {
            event.getEntity().swing(InteractionHand.MAIN_HAND);
            RainbowUtil.setRainbowType(event.getEntity(), 0);
            if (!event.getEntity().isCreative()) {
                stack.shrink(1);
            }
            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!event.getEntity().addItem(wetSponge)) {
                event.getEntity().drop(wetSponge, true);
            }
        }
    }

    @SubscribeEvent
    public static void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        if (AlexsMobs.isAprilFools() && event.getItemStack().is(Items.STICK)
                && !event.getEntity().getCooldowns().isOnCooldown(Items.STICK)) {
            BlockState state = event.getEntity().level().getBlockState(event.getPos());
            boolean flag = false;
            if (state.is(Blocks.SAND)) {
                flag = true;
                event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.SAND_CIRCLE.get().defaultBlockState());
            } else if (state.is(Blocks.RED_SAND)) {
                flag = true;
                event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.RED_SAND_CIRCLE.get().defaultBlockState());
            }
            if (flag) {
                event.setCanceled(true);
                event.getEntity().gameEvent(GameEvent.BLOCK_PLACE);
                event.getEntity().playSound(SoundEvents.SAND_BREAK, 1, 1);
                event.getEntity().getCooldowns().addCooldown(Items.STICK, 30);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onTradeSetup(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FISHERMAN) {
            VillagerTrades.ItemListing ambergrisTrade = new EmeraldsForItemsTrade(AMItemRegistry.AMBERGRIS.get(), 20, 3, 4);
            var list = event.getTrades().get(2);
            list.add(ambergrisTrade);
            event.getTrades().put(2, list);
        }
    }

    @SubscribeEvent
    public static void onWanderingTradeSetup(WandererTradesEvent event) {
        if (AMConfig.wanderingTraderOffers) {
            List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
            List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ANIMAL_DICTIONARY.get(), 4, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ACACIA_BLOSSOM.get(), 3, 2, 2, 1));
            if (AMConfig.cockroachSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.COCKROACH_OOTHECA.get(), 2, 1, 2, 1));
            }
            if (AMConfig.blobfishSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOBFISH_BUCKET.get(), 4, 1, 3, 1));
            }
            if (AMConfig.crocodileSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.CROCODILE_EGG.get().asItem(), 6, 1, 2, 1));
            }
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BEAR_FUR.get(), 1, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.CROCODILE_SCUTE.get(), 5, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ROADRUNNER_FEATHER.get(), 1, 2, 2, 2));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.MOSQUITO_LARVA.get(), 1, 3, 5, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.SOMBRERO.get(), 20, 1, 1, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.BANANA_PEEL.get(), 1, 2, 1, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOOD_SAC.get(), 5, 2, 3, 1));
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        var customData = event.getItemStack().get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("BisonFur") && tag.getBoolean("BisonFur")) {
                event.getToolTip().add(Component.translatable("item.alexsmobs.insulated_with_fur").withStyle(ChatFormatting.AQUA));
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");
        event.addListener(AlexsMobs.PROXY.getCapsidRecipeManager());
    }
}
