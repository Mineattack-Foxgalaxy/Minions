package io.github.skippyall.minions.program.instruction.execution.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.execution.ContinuousInstructionExecution;
import net.minecraft.entity.MovementType;

public class ContinuousWalkExecution implements ContinuousInstructionExecution<MinionRuntime>, InstructionExecution.Stateless<MinionRuntime> {
    @Override
    public void tick(MinionRuntime minion) {
        minion.getMinion().move(MovementType.SELF, minion.getMinion().getHorizontalFacing().getDoubleVector().normalize().multiply(minion.getMinion().getMovementSpeed()));
    }
}
