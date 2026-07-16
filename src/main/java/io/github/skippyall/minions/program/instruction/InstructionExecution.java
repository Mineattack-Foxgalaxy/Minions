package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.supplier.ParameterValueList;

/**
 * Responsible for executing instructions.
 * When an instruction is executed:
 * <li>A new instance is created using the factory</li>
 * <li>{@link InstructionExecution#readArguments(ParameterValueList, Context) readFromParameters} is called</li>
 * <li>{@link InstructionExecution#start(Context) start} is called</li>
 */
public interface InstructionExecution {
    /**
     * Starts the execution.
     */
    default void start(Context context) {}

    /**
     * Continues the execution of this instruction.
     * Called every tick while executing the instruction.
     */
    default void tick(Context context) {}

    /**
     * Called every tick to determine if the execution of this instruction should be stopped.
     * @return <code>true</code> if the instruction is done, <code>false</code> otherwise.
     */
    boolean isDone(Context context);

    default void pause(Context context) {}

    default void resume(Context context) {}

    /**
     * Stops this execution. Is called when isDone returns true, but it may also be called before that.
     * This should undo changes to the minion unless they are supposed to be permanent.
     */
    default void stop(ParameterValueList list, Context context) {}

    /**
     * Initializes the execution with its parameters. The parameters must be defined by the InstructionType
     */
    void readArguments(ParameterValueList arguments, Context context);

    interface Argumentless extends InstructionExecution {
        @Override
        default void readArguments(ParameterValueList arguments, Context context) {}
    }
}
