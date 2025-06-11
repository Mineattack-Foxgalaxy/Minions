package io.github.skippyall.minions.new_program.instruction;

public interface InstructionRun {
    default void tick() {}

    default boolean isDone() {
        return true;
    }
}
