package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.supplier.ParameterValueList;

/**
 * Responsible for executing instructions.
 * When an instruction is executed:
 * <li>A new instance is created using the factory</li>
 * <li>{@link InstructionExecution#readArguments(ParameterValueList, ExecutionContext) readFromParameters} is called</li>
 * <li>{@link InstructionExecution#start(ExecutionContext) start} is called</li>
 */
public interface InstructionExecution {
    /**
     * Starts the execution.
     */
    default void start(ExecutionContext context) {}

    /**
     * Continues the execution of this instruction.
     * Called every tick while executing the instruction.
     */
    default void tick(ExecutionContext context) {}

    /**
     * Called every tick to determine if the execution of this instruction should be stopped.
     * @return <code>true</code> if the instruction is done, <code>false</code> otherwise.
     */
    boolean isDone(ExecutionContext context);

    default void pause(ExecutionContext context) {}

    default void resume(ExecutionContext context) {}

    /**
     * Stops this execution. Is called when isDone returns true, but it may also be called before that.
     * This should undo changes to the minion unless they are supposed to be permanent.
     */
    default void stop(ParameterValueList list, ExecutionContext context) {}

    /**
     * Initializes the execution with its parameters. The parameters must be defined by the InstructionType
     */
    void readArguments(ParameterValueList arguments, ExecutionContext context);

    interface Argumentless extends InstructionExecution {
        @Override
        default void readArguments(ParameterValueList arguments, ExecutionContext context) {}
    }
}
