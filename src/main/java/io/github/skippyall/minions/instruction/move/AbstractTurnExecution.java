package io.github.skippyall.minions.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public abstract class AbstractTurnExecution implements InstructionExecution<MinionRuntime> {
    protected float targetYaw;
    protected float targetPitch;

    private static final float anglePerTick = 10;

    @Override
    public void tick(MinionRuntime minion) {
        float rotateYaw = targetYaw - minion.getMinion().getYaw();
        float rotatePitch = targetPitch - minion.getMinion().getPitch();

        minion.getMinion().getMinionActionPack().turn(Math.min(rotateYaw, anglePerTick), Math.min(rotatePitch, anglePerTick));
    }

    @Override
    public boolean isDone(MinionRuntime minion) {
        return Math.abs(targetYaw - minion.getMinion().getYaw()) < 0.001F && Math.abs(targetPitch - minion.getMinion().getPitch()) < 0.001F;
    }

    @Override
    public void save(WriteView view, MinionRuntime runtime) {
        view.putFloat("targetYaw", targetYaw);
        view.putFloat("targetPitch", targetPitch);
    }

    @Override
    public void load(ReadView view, MinionRuntime runtime) {
        targetYaw = view.getFloat("targetYaw", 0);
        targetPitch = view.getFloat("targetPitch", 0);
    }
}
