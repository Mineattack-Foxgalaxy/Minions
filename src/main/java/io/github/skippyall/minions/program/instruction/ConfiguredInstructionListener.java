package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.handler.Parameter;

public interface ConfiguredInstructionListener {
    default void onRun(ConfiguredInstruction instruction, InstructionRuntime runtime, int id) {}

    default void onStop(ConfiguredInstruction instruction) {}

    default void onSupplierChange(ConfiguredInstruction instruction, Parameter<?> parameter) {}

    default void onConsumerChange(ConfiguredInstruction instruction, Parameter<?> parameter) {}

    default void onInstructionRemove(ConfiguredInstruction instruction) {}
}
