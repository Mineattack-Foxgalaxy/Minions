package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.execution.ContinuousInstructionExecution;
import net.minecraft.world.entity.MoverType;

public class ContinuousWalkExecution implements ContinuousInstructionExecution<MinionRuntime>, InstructionExecution.Argumentless<MinionRuntime> {
    public static final Codec<ContinuousWalkExecution> CODEC = MapCodec.unitCodec(ContinuousWalkExecution::new);

    @Override
    public void tick(MinionRuntime minion) {
        minion.getMinion().move(MoverType.SELF, minion.getMinion().getDirection().getUnitVec3().normalize().scale(minion.getMinion().getSpeed()));
    }
}
