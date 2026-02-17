package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

/**
 * Responsible for executing instructions.
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
     * Called before and after {@code tick} to determine if the execution of this instruction should be stopped.
     * @return <code>true</code> if the instruction is done, <code>false</code> otherwise.
     */
    boolean isDone(R runtime);

    /**
     * Called when the instruction is paused. This freezes the instruction in its current state.
     */
    default void pause(R runtime) {}

    default void resume(R runtime) {}

    /**
     * Stops this execution. Is called when isDone returns true, but it may also be called before that.
     * This should undo temporary changes to the minion.
     *
     * @param runtime The runtime that was executing this instruction.
     */
    default void stop(R runtime, ValueConsumerList<R> valueConsumers) {}

    /**
     * Initializes the execution with its parameters. The parameters must be defined by the InstructionType
     * @param arguments The arguments to initialize the execution
     * @param runtime The runtime should be used to resolve the arguments
     */
    void readArguments(ValueSupplierList<R> arguments, R runtime);

    /**
     * Saves the execution, e.g. when the server is closed.
     */
    void save(WriteView view, R runtime);

    /**
     * Loads the execution, e.g. when the server is started.
     */
    void load(ReadView view, R runtime);

    interface Stateless<R extends InstructionRuntime<R>> extends InstructionExecution<R> {
        @Override
        default void readArguments(ValueSupplierList<R> arguments, R runtime) {}

        @Override
        default void save(WriteView view, R runtime) {}

        @Override
        default void load(ReadView view, R runtime) {}
    }
}
