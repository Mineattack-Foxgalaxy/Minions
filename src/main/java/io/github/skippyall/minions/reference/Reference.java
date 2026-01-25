package io.github.skippyall.minions.reference;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface Reference extends TooltipAppender {
    Codec<Reference> CODEC = MinionRegistries.REFERENCE_CODEC.getCodec().dispatch(Reference::getCodec, Function.identity());
    ComponentType<Reference> COMPONENT_TYPE = ComponentType.<Reference>builder().codec(CODEC).build();

    MapCodec<? extends Reference> getCodec();

    static void register() {
        Registry.register(MinionRegistries.REFERENCE_CODEC, Identifier.of(Minions.MOD_ID, "instruction"), InstructionReference.CODEC);

        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, "reference"), COMPONENT_TYPE);
        PolymerComponent.registerDataComponent(COMPONENT_TYPE);

        ComponentTooltipAppenderRegistry.addFirst(COMPONENT_TYPE);
    }
}
