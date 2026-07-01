package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.instruction.InstructionExecution;

public interface ContinuousInstructionExecution extends InstructionExecution {
    @Override
    default boolean isDone(ExecutionContext context) {
        return false;
    }
}
