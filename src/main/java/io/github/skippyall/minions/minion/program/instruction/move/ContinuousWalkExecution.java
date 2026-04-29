package io.github.skippyall.minions.minion.program.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.execution.ContinuousInstructionExecution;
import net.minecraft.world.entity.MoverType;

public class ContinuousWalkExecution implements ContinuousInstructionExecution<MinionRuntime>, InstructionExecution.Stateless<MinionRuntime> {
    @Override
    public void tick(MinionRuntime minion) {
        minion.getMinion().move(MoverType.SELF, minion.getMinion().getDirection().getUnitVec3().normalize().scale(minion.getMinion().getSpeed()));
    }
}
