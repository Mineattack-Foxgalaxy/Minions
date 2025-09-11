package io.github.skippyall.minions.new_program.instruction.execution;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.instruction.InstructionExecution;

public interface ContinuousInstructionExecution<R> extends InstructionExecution<R> {
    @Override
    default boolean isDone(MinionFakePlayer minion) {
        return false;
    }
}
