package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.clipboard.Clipboard;
import io.github.skippyall.minions.module.MinionModule;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public class MinionComponentTypes {
    public static final DataComponentType<UUID> MINION_DATA = register("minion_data", DataComponentType.<UUID>builder().persistent(UUIDUtil.AUTHLIB_CODEC).build());
    public static final DataComponentType<MinionModule> MODULE = register("minion_module", DataComponentType.<MinionModule>builder().persistent(MinionModule.CODEC).build());
    public static final DataComponentType<Clipboard> REFERENCE = register("reference", DataComponentType.<Clipboard>builder().persistent(Clipboard.CODEC).build());

    private static <T extends DataComponentType<?>> T register(String name, T type) {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, name), type);
        PolymerComponent.registerDataComponent(type);
        return type;
    }

    public static void register() {
        ComponentTooltipAppenderRegistry.addFirst(MinionComponentTypes.REFERENCE);
    }
}
