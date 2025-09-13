package io.github.skippyall.minions.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public record MinionModule(List<InstructionType<?>> instructions) {
    public static final Codec<MinionModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MinionRegistries.INSTRUCTION_TYPES.getCodec().listOf().fieldOf("instructions").forGetter(MinionModule::instructions)
            ).apply(instance, MinionModule::new)
    );

    public static final ComponentType<MinionModule> COMPONENT_TYPE = ComponentType.<MinionModule>builder().codec(CODEC).build();

    public static final MinionModule EMPTY = new MinionModule(List.of());

    public MinionModule(List<InstructionType<?>> instructions) {
        this.instructions = List.copyOf(instructions);
    }

    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, "minion_module"), COMPONENT_TYPE);
        PolymerComponent.registerDataComponent(COMPONENT_TYPE);
    }
}
