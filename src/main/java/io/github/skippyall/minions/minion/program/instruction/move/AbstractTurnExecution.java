package io.github.skippyall.minions.minion.program.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class AbstractTurnExecution implements InstructionExecution<MinionRuntime> {
    protected float targetYaw;
    protected float targetPitch;

    private static final float anglePerTick = 10;

    @Override
    public void tick(MinionRuntime minion) {
        float rotateYaw = targetYaw - minion.getMinion().getYRot();
        float rotatePitch = targetPitch - minion.getMinion().getXRot();

        minion.getMinion().getMinionActionPack().turn(Math.min(rotateYaw, anglePerTick), Math.min(rotatePitch, anglePerTick));
    }

    @Override
    public boolean isDone(MinionRuntime minion) {
        return Math.abs(targetYaw - minion.getMinion().getYRot()) < 0.001F && Math.abs(targetPitch - minion.getMinion().getXRot()) < 0.001F;
    }

    @Override
    public void save(ValueOutput view, MinionRuntime runtime) {
        view.putFloat("targetYaw", targetYaw);
        view.putFloat("targetPitch", targetPitch);
    }

    @Override
    public void load(ValueInput view, MinionRuntime runtime) {
        targetYaw = view.getFloatOr("targetYaw", 0);
        targetPitch = view.getFloatOr("targetPitch", 0);
    }
}
