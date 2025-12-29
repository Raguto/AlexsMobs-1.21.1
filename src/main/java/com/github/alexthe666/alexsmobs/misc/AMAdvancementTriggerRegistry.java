package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for Alex's Mobs advancement triggers
 * Updated for 1.21.1 - uses DeferredRegister instead of CriteriaTriggers.register
 */
public class AMAdvancementTriggerRegistry {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> MOSQUITO_SICK = TRIGGERS.register("mosquito_sick", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> EMU_DODGE = TRIGGERS.register("emu_dodge", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> STOMP_LEAFCUTTER_ANTHILL = TRIGGERS.register("stomp_leafcutter_anthill", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> BALD_EAGLE_CHALLENGE = TRIGGERS.register("bald_eagle_challenge", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> VOID_WORM_SUMMON = TRIGGERS.register("void_worm_summon", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> VOID_WORM_SPLIT = TRIGGERS.register("void_worm_split", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> VOID_WORM_SLAY_HEAD = TRIGGERS.register("void_worm_kill", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> SEAGULL_STEAL = TRIGGERS.register("seagull_steal", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> LAVIATHAN_FOUR_PASSENGERS = TRIGGERS.register("laviathan_four_passengers", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> TRANSMUTE_1000_ITEMS = TRIGGERS.register("transmute_1000_items", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> UNDERMINE_UNDERMINER = TRIGGERS.register("undermine_underminer", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> ELEPHANT_SWAG = TRIGGERS.register("elephant_swag", AMAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, AMAdvancementTrigger> SKUNK_SPRAY = TRIGGERS.register("skunk_spray", AMAdvancementTrigger::new);
}
