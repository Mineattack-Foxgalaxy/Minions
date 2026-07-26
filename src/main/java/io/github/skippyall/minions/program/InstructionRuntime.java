package io.github.skippyall.minions.program;

import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.server.MinecraftServer;

public interface InstructionRuntime {
    boolean isInstructionEnabled(InstructionType type);

    int addInstruction(ExecutingInstruction executingInstruction);

    MinecraftServer getServer();

    Context getContext();

    int run(InstructionType instructionType, ParameterValueList arguments);
}
