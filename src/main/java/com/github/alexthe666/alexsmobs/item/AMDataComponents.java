package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.data.TabIconDisplay;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = 
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AlexsMobs.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TabIconDisplay>> TAB_ICON_DISPLAY = 
            DATA_COMPONENTS.register("tab_icon_display", () -> DataComponentType.<TabIconDisplay>builder()
                    .persistent(TabIconDisplay.CODEC)
                    .networkSynchronized(TabIconDisplay.STREAM_CODEC)
                    .build());
}
