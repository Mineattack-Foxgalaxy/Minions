package io.github.skippyall.minions.program.returnvalue;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Argument;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueConsumers {
    public static <R extends InstructionRuntime<R>> Codec<ValueConsumer<?,R>> createValueConsumersCodec(Codec<ValueConsumerType<R>> codec) {
        return codec.dispatch(
                "type",
                ValueConsumer::getType,
                type ->
                        MinionRegistries.VALUE_TYPES.getCodec().<ValueConsumer<?,R>>dispatch(
                                ValueConsumer::getValueType,
                                valueType -> type.getCodec(valueType).fieldOf("valueType")
                        ).fieldOf("valueType")
        );
    }
}
