package io.github.skippyall.minions.new_program.instruction;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.argument.ArgumentList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

/**
 * Responsible for executing instructions.
 * When an instruction is executed:
 * <li>A new instance is created using the factory</li>
 * <li>{@link InstructionExecution#readArguments(ArgumentList, MinionFakePlayer) readFromParameters} is called</li>
 * <li>{@link InstructionExecution#start(MinionFakePlayer) start} is called</li>
 * @param <R>
 */
public interface InstructionExecution<R> {
    /**
     * Starts the execution.
     * @param minion
     */
    default void start(MinionFakePlayer minion) {}

    /**
     * Continues the execution of this instruction.
     * Called every tick while executing the instruction.
     * @param minion
     */
    default void tick(MinionFakePlayer minion) {}

    /**
     * Called every tick to determine if the execution of this instruction should be stopped.
     * Shouldn't
     * @param minion The minion executing the instruction
     * @return <code>true</code> if the instruction is done, <code>false</code> otherwise.
     */
    boolean isDone(MinionFakePlayer minion);

    /**
     * Stops this execution. This is guaranteed to be called if {@link InstructionExecution#isDone(MinionFakePlayer) isDone}
     * returns true after ticking the execution, but it may also be called before that.
     * In this case, the return value is ignored unless this is a
     * {@link io.github.skippyall.minions.new_program.instruction.execution.ContinuousInstructionExecution ContinuousInstructionExecution}.</br>
     * This should undo changes to the minion unless they are supposed to be permanent.
     *
     * @param minion The minion that was executing this instruction.
     * @return The return value of the instruction
     */
    R stop(MinionFakePlayer minion);

    /**
     * Initializes the execution with its arguments.
     * @param arguments The arguments to initialize the execution
     * @param minion The minion should be used to resolve the arguments
     */
    void readArguments(ArgumentList arguments, MinionFakePlayer minion);

    /**
     * Saves the execution, e.g. when the server is closed.
     */
    void save(WriteView view, MinionFakePlayer minion);

    /**
     * Loads the execution, e.g. when the server is started.
     */
    void load(ReadView view, MinionFakePlayer minion);
}
