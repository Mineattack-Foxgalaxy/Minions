package io.github.skippyall.minions.program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.program.InstructionRuntime;

public class Arguments {
    public static <R extends InstructionRuntime<R>> Codec<Argument<?,R>> createArgumentCodec(Codec<ArgumentType<R>> codec) {
        return codec.dispatch(
                "type",
                Argument::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.getCodec().<Argument<?,R>>dispatch(
                                Argument::getValueType,
                                valueType -> type.getCodec(valueType).fieldOf("valueType")
                        ).fieldOf("valueType")
        );
    }
}
