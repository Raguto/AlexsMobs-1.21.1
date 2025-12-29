package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
// AMEnchantmentRegistry disabled
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.AMDataComponents;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.network.*;
import com.github.alexthe666.alexsmobs.misc.*;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMLeafcutterAntBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnStructureModifier;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

@Mod(AlexsMobs.MODID)
public class AlexsMobs {
    
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alexsmobs";
    // TODO: Remove old SimpleChannel networking - replaced with PayloadRegistrar system
    // public static final SimpleChannel NETWORK_WRAPPER;
    public static final CommonProxy PROXY = unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static boolean isAprilFools = false;
    private static boolean isHalloween = false;

    public AlexsMobs(net.neoforged.fml.ModContainer modContainer, IEventBus modEventBus) {
        IEventBus modBusEvent = modEventBus;
        modBusEvent.addListener(this::setup);
        modBusEvent.addListener(this::setupClient);
        modBusEvent.addListener(this::onModConfigEvent);
        modBusEvent.addListener(this::onModConfigLoading);
        modBusEvent.addListener(this::setupEntityModelLayers);
        modBusEvent.addListener(this::onRegisterSpawnPlacements);
        modBusEvent.addListener(AMEntityRegistry::initializeAttributes);
        AMBlockRegistry.DEF_REG.register(modBusEvent);
        AMEntityRegistry.DEF_REG.register(modBusEvent);
        AMItemRegistry.DEF_REG.register(modBusEvent);
        AMTileEntityRegistry.DEF_REG.register(modBusEvent);
        AMPointOfInterestRegistry.DEF_REG.register(modBusEvent);
        AMFeatureRegistry.DEF_REG.register(modBusEvent);
        AMSoundRegistry.DEF_REG.register(modBusEvent);
        AMParticleRegistry.DEF_REG.register(modBusEvent);
        AMPaintingRegistry.DEF_REG.register(modBusEvent);
        AMEffectRegistry.EFFECT_DEF_REG.register(modBusEvent);
        AMEffectRegistry.POTION_DEF_REG.register(modBusEvent);
        // TODO 1.21: AMEnchantmentRegistry.DEF_REG.register(modBusEvent); - disabled, enchantments now data-driven
        AMMenuRegistry.DEF_REG.register(modBusEvent);
        AMRecipeRegistry.DEF_REG.register(modBusEvent);
        AMLootRegistry.DEF_REG.register(modBusEvent);
        AMBannerRegistry.DEF_REG.register(modBusEvent);
        AMCreativeTabRegistry.DEF_REG.register(modBusEvent);
        AMAdvancementTriggerRegistry.TRIGGERS.register(modBusEvent);
        AMDataComponents.DATA_COMPONENTS.register(modBusEvent);

        // Register networking
        modBusEvent.addListener(com.github.alexthe666.alexsmobs.network.AMNetworking::register);

        final DeferredRegister<com.mojang.serialization.MapCodec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
        biomeModifiers.register(modBusEvent);
        biomeModifiers.register("am_mob_spawns", AMMobSpawnBiomeModifier::makeCodec);
        biomeModifiers.register("am_leafcutter_ant_spawns", AMLeafcutterAntBiomeModifier::makeCodec);
        final DeferredRegister<com.mojang.serialization.MapCodec<? extends StructureModifier>> structureModifiers = DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, AlexsMobs.MODID);
        structureModifiers.register(modBusEvent);
        structureModifiers.register("am_structure_spawns", AMMobSpawnStructureModifier::makeCodec);
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
        PROXY.init();
        // ServerEvents uses @EventBusSubscriber annotation for auto-registration
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        isAprilFools = calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
        isHalloween = calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) >= 29 && calendar.get(Calendar.DATE) <= 31;
    }

    public static boolean isAprilFools() {
        return isAprilFools || AMConfig.superSecretSettings;
    }

    public static boolean isHalloween() {
        return isHalloween || AMConfig.superSecretSettings;
    }

    private void setupEntityModelLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        AMModelLayers.register(event);
    }

    private void onRegisterSpawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        AMEntityRegistry.registerSpawnPlacements(event);
    }

    public void onModConfigEvent(final ModConfigEvent.Reloading event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            AMConfig.bake(config);
        }
        BiomeConfig.init();
    }

    public void onModConfigLoading(final ModConfigEvent.Loading event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            AMConfig.bake(config);
        }
        BiomeConfig.init();
    }

    // Networking helper methods using NeoForge 1.21 PacketDistributor
    public static void sendMSGToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload message) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(message);
    }

    public static void sendMSGToAll(net.minecraft.network.protocol.common.custom.CustomPacketPayload message) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, message);
            }
        }
    }

    public static void sendNonLocal(net.minecraft.network.protocol.common.custom.CustomPacketPayload msg, ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, msg);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // TODO: Old SimpleChannel packet registration removed - now done in AMNetworking.register()
        // All message classes need to be converted to CustomPacketPayload format
        event.enqueueWork(AMItemRegistry::init);
        event.enqueueWork(AMItemRegistry::initDispenser);
        // TODO 1.21: AMAdvancementTriggerRegistry.init();
        AMEffectRegistry.init();
        AMRecipeRegistry.init();
        PROXY.initPathfinding();
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(PROXY::clientInit);
    }

    private static <T> T unsafeRunForDist(java.util.function.Supplier<java.util.function.Supplier<T>> clientTarget, java.util.function.Supplier<java.util.function.Supplier<T>> serverTarget) {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }

}
