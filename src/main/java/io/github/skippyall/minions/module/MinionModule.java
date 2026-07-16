package io.github.skippyall.minions.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.registration.MinionRegistries;

import java.util.List;

public record MinionModule(List<InstructionType> instructions, List<SpecialAbility> specialAbilities) {
    public static final Codec<MinionModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MinionRegistries.INSTRUCTION_TYPES.byNameCodec().listOf().fieldOf("instructions").forGetter(MinionModule::instructions),
                    MinionRegistries.SPECIAL_ABILITIES.byNameCodec().listOf().fieldOf("specialAbilities").forGetter(MinionModule::specialAbilities)
            ).apply(instance, MinionModule::new)
    );

    public static final MinionModule EMPTY = new MinionModule(List.of());

    public MinionModule(List<InstructionType> instructions) {
        this(instructions, List.of());
    }

    public MinionModule(List<InstructionType> instructions, List<SpecialAbility> specialAbilities) {
        this.instructions = List.copyOf(instructions);
        this.specialAbilities = List.copyOf(specialAbilities);
    }
}
