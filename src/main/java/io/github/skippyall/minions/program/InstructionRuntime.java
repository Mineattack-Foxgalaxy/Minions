package io.github.skippyall.minions.program;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.argument.ArgumentType;
import io.github.skippyall.minions.program.argument.Arguments;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.returnvalue.ValueConsumerType;
import net.minecraft.registry.Registry;

public interface InstructionRuntime<R extends InstructionRuntime<R>> {
    Registry<ArgumentType<R>> getArgumentTypeRegistry();

    Registry<InstructionType<R>> getInstructionTypeRegistry();

    Registry<ValueConsumerType<R>> getValueConsumerRegistry();

    default Codec<ArgumentType<R>> getArgumentTypeCodec() {
        return getArgumentTypeRegistry().getCodec();
    }

    default Codec<Argument<?,R>> getArgumentCodec() {
        return Arguments.createArgumentCodec(getArgumentTypeCodec());
    }

    default Codec<ArgumentList<R>> getArgumentListCodec() {
        return ArgumentList.getCodec(getArgumentTypeCodec());
    }
}
