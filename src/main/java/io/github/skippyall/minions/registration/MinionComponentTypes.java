package io.github.skippyall.minions.registration;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.clipboard.Clipboard;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class MinionComponentTypes {
    public static final ComponentType<UUID> MINION_DATA = register("minion_data", ComponentType.<UUID>builder().codec(Uuids.CODEC).build());
    public static final ComponentType<MinionModule> MODULE = register("minion_module", ComponentType.<MinionModule>builder().codec(MinionModule.CODEC).build());
    public static final ComponentType<Clipboard> REFERENCE = register("reference", ComponentType.<Clipboard>builder().codec(Clipboard.CODEC).build());

    private static <T extends ComponentType<?>> T register(String name, T type) {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, name), type);
        PolymerComponent.registerDataComponent(type);
        return type;
    }

    public static void register() {
        ComponentTooltipAppenderRegistry.addFirst(MinionComponentTypes.REFERENCE);
    }
}
