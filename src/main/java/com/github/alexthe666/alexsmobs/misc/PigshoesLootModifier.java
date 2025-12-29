package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class PigshoesLootModifier implements IGlobalLootModifier {

    public static final Supplier<MapCodec<PigshoesLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new PigshoesLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table ID since codec doesn't load conditions from JSON
    private static final ResourceLocation PIGLIN_BARTERING = ResourceLocation.withDefaultNamespace("gameplay/piglin_bartering");

    private final LootItemCondition[] conditions;

    public PigshoesLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Hardcoded check for piglin bartering
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (lootTableId.equals(PIGLIN_BARTERING)) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            if (context.getRandom().nextFloat() <= AMConfig.tusklinShoesBarteringChance) {
                generatedLoot.add(new ItemStack(AMItemRegistry.PIGSHOES.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}