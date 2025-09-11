package io.github.skippyall.minions.new_program.instruction.execution;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.instruction.InstructionExecution;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

/**
 * An <code>InstructionExecution</code> that takes a predefined time to execute.
 * The timer must be set with <code>setTimer</code> when reading from parameters.
 * Saving and loading of the timer is automatic if the super method is called by the subclass.
 */
public abstract class TimedInstructionExecution<T> implements InstructionExecution<T> {
    int timer;

    public int getTimer() {
        return timer;
    }

    protected void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public void tick(MinionFakePlayer minion) {
        timer--;
    }

    @Override
    public boolean isDone(MinionFakePlayer minion) {
        return timer > 0;
    }

    @Override
    public void save(WriteView view, MinionFakePlayer minion) {
        view.putInt("timer", timer);
    }

    @Override
    public void load(ReadView view, MinionFakePlayer minion) {
        timer = view.getInt("timer", 0);
    }
}
