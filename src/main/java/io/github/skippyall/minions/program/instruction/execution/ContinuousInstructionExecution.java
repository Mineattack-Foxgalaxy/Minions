package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.InstructionExecution;

public interface ContinuousInstructionExecution extends InstructionExecution {
    @Override
    default boolean isDone(Context context) {
        return false;
    }
}
