package io.github.skippyall.minions.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionType;

import java.util.List;

public record MinionModule(List<InstructionType<MinionRuntime>> instructions, List<SpecialAbility> specialAbilities) {
    public static final Codec<MinionModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MinionRegistries.INSTRUCTION_TYPES.getCodec().listOf().fieldOf("instructions").forGetter(MinionModule::instructions),
                    MinionRegistries.SPECIAL_ABILITIES.getCodec().listOf().fieldOf("specialAbilities").forGetter(MinionModule::specialAbilities)
            ).apply(instance, MinionModule::new)
    );

    public static final MinionModule EMPTY = new MinionModule(List.of());

    public MinionModule(List<InstructionType<MinionRuntime>> instructions) {
        this(instructions, List.of());
    }

    public MinionModule(List<InstructionType<MinionRuntime>> instructions, List<SpecialAbility> specialAbilities) {
        this.instructions = List.copyOf(instructions);
        this.specialAbilities = List.copyOf(specialAbilities);
    }
}
