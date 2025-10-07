package io.github.skippyall.minions.program;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.argument.Arguments;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import io.github.skippyall.minions.program.argument.SpecificArgumentType;
import net.minecraft.registry.Registry;

public interface InstructionRuntime<R extends InstructionRuntime<R>> {
    Registry<GenericArgumentType<R>> getGenericArgumentTypeRegistry();

    default Codec<GenericArgumentType<R>> getGenericArgumentTypeCodec() {
        return getGenericArgumentTypeRegistry().getCodec();
    }

    default Codec<SpecificArgumentType<?,?,R>> getSpecificArgumentTypeCodec() {
        return Arguments.createSpecificTypeCodec(getGenericArgumentTypeCodec());
    }

    default Codec<Argument<?,?,R>> getArgumentCodec() {
        return Arguments.createArgumentCodec(getGenericArgumentTypeCodec());
    }

    default Codec<ArgumentList<R>> getArgumentListCodec() {
        return ArgumentList.getCodec(getGenericArgumentTypeCodec());
    }
}
