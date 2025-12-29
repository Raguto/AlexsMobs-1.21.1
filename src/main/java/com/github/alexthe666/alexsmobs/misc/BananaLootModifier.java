package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class BananaLootModifier implements IGlobalLootModifier {

    public static final Supplier<MapCodec<BananaLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new BananaLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table ID since codec doesn't load conditions from JSON
    private static final ResourceLocation JUNGLE_LEAVES = ResourceLocation.withDefaultNamespace("blocks/jungle_leaves");

    private final LootItemCondition[] conditions;

    public BananaLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Hardcoded check for jungle leaves
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (lootTableId.equals(JUNGLE_LEAVES)) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
        if (AMConfig.bananasDropFromLeaves){
            ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
            RandomSource random = context.getRandom();
            if(ctxTool != null){
                int silkTouch = ctxTool.getEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH));
                if(silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem){
                    return generatedLoot;
                }
            }
            int bonusLevel = ctxTool != null ? 0 /* TODO 1.21: Enchantments are data-driven */ : 0;
            int bananaStep = (int)Math.floor(AMConfig.bananaChance * 0.1F);
            int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.BANANA.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}