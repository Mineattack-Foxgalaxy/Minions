package io.github.skippyall.minions.util;

import io.github.skippyall.minions.program.conversion.ValueConverterType;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.Registry;
import org.jspecify.annotations.Nullable;

public class RegistryUtil {
    public static <T> @Nullable Registry<T> getRegistry(@Nullable T object) {
        //noinspection unchecked
        return (Registry<T>) switch (object) {
            case ValueSupplierType _ -> MinionRegistries.VALUE_SUPPLIER_TYPES;
            case ValueConsumerType _ -> MinionRegistries.VALUE_CONSUMER_TYPES;
            case InstructionType _ -> MinionRegistries.INSTRUCTION_TYPES;
            case ValueType<?> _ -> MinionRegistries.VALUE_TYPES;
            case ValueConverterType<?> _ -> MinionRegistries.VALUE_CONVERTER_TYPES;
            case null, default -> null;
        };
    }
}
