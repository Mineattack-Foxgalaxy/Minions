package io.github.skippyall.minions.program;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.consumer.ValueConsumer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;

public interface InstructionRuntime<R extends InstructionRuntime<R>> {
    Registry<ValueConsumerType<R>> getValueConsumerTypeRegistry();

    boolean isInstructionEnabled(InstructionType<R> type);

    int addInstruction(ExecutingInstruction<R> executingInstruction);

    MinecraftServer getServer();

    default Codec<ValueConsumerType<R>> getValueConsumerTypeCodec() {
        return getValueConsumerTypeRegistry().byNameCodec();
    }

    default Codec<ValueConsumer<?,R>> getValueConsumerCodec() {
        return ValueConsumer.createValueConsumerCodec(getValueConsumerTypeCodec());
    }

    default Codec<ValueConsumerList<R>> getValueConsumerListCodec() {
        return ValueConsumerList.getCodec(getValueConsumerCodec());
    }
}
