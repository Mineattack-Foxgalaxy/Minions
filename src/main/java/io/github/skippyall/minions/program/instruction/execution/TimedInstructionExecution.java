package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

/**
 * An <code>InstructionExecution</code> that takes a predefined time to execute.
 * The timer must be set with <code>setTimer</code> when reading from parameters.
 * Saving and loading of the timer is automatic if the super method is called by the subclass.
 */
public abstract class TimedInstructionExecution<R extends InstructionRuntime<R>> implements InstructionExecution<R> {
    int timer;

    public int getTimer() {
        return timer;
    }

    protected void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public void tick(R minion) {
        timer--;
    }

    @Override
    public boolean isDone(R minion) {
        return timer > 0;
    }

    @Override
    public void save(WriteView view, R minion) {
        view.putInt("timer", timer);
    }

    @Override
    public void load(ReadView view, R minion) {
        timer = view.getInt("timer", 0);
    }
}
