package io.github.skippyall.minions.new_program.instruction;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.ParameterList;
import io.github.skippyall.minions.program.variables.Type;

import java.util.List;

public interface Instruction {
    List<Type<?>> getParameterTypes();

    Type<?> getReturnType();

    InstructionRun run(MinionFakePlayer minion, ParameterList params);
}
