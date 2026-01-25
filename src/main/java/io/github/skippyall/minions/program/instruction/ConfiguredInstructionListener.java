package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.listener.SerializableListenerManager;

public interface ConfiguredInstructionListener extends SerializableListenerManager.SerializableListener {
    default void onRun(ConfiguredInstruction<?> instruction) {}

    default void onStop(ConfiguredInstruction<?> instruction) {}

    default void onSupplierChange(ConfiguredInstruction<?> instruction, Parameter<?> parameter) {}

    default void onConsumerChange(ConfiguredInstruction<?> instruction, Parameter<?> parameter) {}

    default void onInstructionRemove(ConfiguredInstruction<?> instruction) {}
}
