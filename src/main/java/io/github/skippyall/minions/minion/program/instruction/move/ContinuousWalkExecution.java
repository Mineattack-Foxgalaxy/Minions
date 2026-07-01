package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.execution.ContinuousInstructionExecution;
import net.minecraft.world.entity.MoverType;

public class ContinuousWalkExecution implements ContinuousInstructionExecution, InstructionExecution.Argumentless {
    public static final Codec<ContinuousWalkExecution> CODEC = MapCodec.unitCodec(ContinuousWalkExecution::new);

    @Override
    public void tick(ExecutionContext context) {
        MinionFakePlayer minion = context.getOrThrow(MinionRuntime.MINION_KEY);
        minion.move(MoverType.SELF, minion.getDirection().getUnitVec3().normalize().scale(minion.getSpeed()));
    }
}
