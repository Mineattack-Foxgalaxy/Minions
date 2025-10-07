package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.argument.ArgumentList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

/**
 * Responsible for executing instructions.
 * When an instruction is executed:
 * <li>A new instance is created using the factory</li>
 * <li>{@link InstructionExecution#readArguments(ArgumentList, R) readFromParameters} is called</li>
 * <li>{@link InstructionExecution#start(R) start} is called</li>
 * @param <Return>
 */
public interface InstructionExecution<Return, R> {
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

    /**
     * Stops this execution. Is called when isDone returns true, but it may also be called before that.
     * In this case, the return value is ignored.
     * This should undo changes to the minion unless they are supposed to be permanent.
     *
     * @param runtime The runtime that was executing this instruction.
     * @return The return value of the instruction
     */
    Return stop(R runtime);

    /**
     * Initializes the execution with its arguments.
     * @param arguments The arguments to initialize the execution
     * @param runtime The runtime should be used to resolve the arguments
     */
    void readArguments(ArgumentList<R> arguments, R runtime);

    /**
     * Saves the execution, e.g. when the server is closed.
     */
    void save(WriteView view, R runtime);

    /**
     * Loads the execution, e.g. when the server is started.
     */
    void load(ReadView view, R runtime);
}
