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
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AncientDartLootModifier implements IGlobalLootModifier {

    public static final Supplier<MapCodec<AncientDartLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new AncientDartLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table IDs since codec doesn't load conditions from JSON
    private static final ResourceLocation JUNGLE_TEMPLE = ResourceLocation.withDefaultNamespace("chests/jungle_temple");
    private static final ResourceLocation JUNGLE_TEMPLE_DISPENSER = ResourceLocation.withDefaultNamespace("chests/jungle_temple_dispenser");

    private final LootItemCondition[] conditions;

    public AncientDartLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Hardcoded check for jungle temple chests
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (lootTableId.equals(JUNGLE_TEMPLE) || lootTableId.equals(JUNGLE_TEMPLE_DISPENSER)) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            generatedLoot.add(new ItemStack(AMItemRegistry.ANCIENT_DART.get()));
        }
        return generatedLoot;
    }


    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

}