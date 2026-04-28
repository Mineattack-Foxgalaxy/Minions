package io.github.skippyall.minions.program;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.consumer.ValueConsumer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import net.minecraft.registry.Registry;

public interface InstructionRuntime<R extends InstructionRuntime<R>> {
    Registry<ValueSupplierType<R>> getArgumentTypeRegistry();

    Registry<InstructionType<R>> getInstructionTypeRegistry();

    Registry<ValueConsumerType<R>> getValueConsumerTypeRegistry();

    boolean isInstructionEnabled(InstructionType<R> type);

    default Codec<ValueSupplierType<R>> getArgumentTypeCodec() {
        return getArgumentTypeRegistry().getCodec();
    }

    default Codec<ValueSupplier<?,R>> getArgumentCodec() {
        return ValueSupplier.createArgumentCodec(getArgumentTypeCodec());
    }

    default Codec<ValueSupplierList<R>> getArgumentListCodec() {
        return ValueSupplierList.getCodec(getArgumentCodec());
    }

    default Codec<ValueConsumerType<R>> getValueConsumerTypeCodec() {
        return getValueConsumerTypeRegistry().getCodec();
    }

    default Codec<ValueConsumer<?,R>> getValueConsumerCodec() {
        return ValueConsumer.createValueConsumerCodec(getValueConsumerTypeCodec());
    }

    default Codec<ValueConsumerList<R>> getValueConsumerListCodec() {
        return ValueConsumerList.getCodec(getValueConsumerCodec());
    }
}
