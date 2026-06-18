package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ParameterValueList;

/**
 * Responsible for executing instructions.
 * When an instruction is executed:
 * <li>A new instance is created using the factory</li>
 * <li>{@link InstructionExecution#readArguments(ParameterValueList, R) readFromParameters} is called</li>
 * <li>{@link InstructionExecution#start(R) start} is called</li>
 */
public interface InstructionExecution<R extends InstructionRuntime<R>> {
    /**
     * Starts the execution.
     */
    default void start(R runtime) {}

    /**
     * Continues the execution of this instruction.
     * Called every tick while executing the instruction.
     */
    default void tick(R runtime) {}

    /**
     * Called every tick to determine if the execution of this instruction should be stopped.
     * @return <code>true</code> if the instruction is done, <code>false</code> otherwise.
     */
    boolean isDone(R runtime);

    default void pause(R runtime) {}

    default void resume(R runtime) {}

    /**
     * Stops this execution. Is called when isDone returns true, but it may also be called before that.
     * This should undo changes to the minion unless they are supposed to be permanent.
     *
     * @param runtime The runtime that was executing this instruction.
     */
    default void stop(ParameterValueList list, R runtime) {}

    /**
     * Initializes the execution with its parameters. The parameters must be defined by the InstructionType
     * @param arguments The arguments to initialize the execution
     * @param runtime The runtime should be used to resolve the arguments
     */
    void readArguments(ParameterValueList arguments, R runtime);

    interface Argumentless<R extends InstructionRuntime<R>> extends InstructionExecution<R> {
        @Override
        default void readArguments(ParameterValueList arguments, R runtime) {}
    }
}
