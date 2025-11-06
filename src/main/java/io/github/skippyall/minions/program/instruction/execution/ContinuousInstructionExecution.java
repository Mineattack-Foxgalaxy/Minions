package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;

public interface ContinuousInstructionExecution<R extends InstructionRuntime<R>> extends InstructionExecution<R> {
    @Override
    default boolean isDone(R minion) {
        return false;
    }
}
