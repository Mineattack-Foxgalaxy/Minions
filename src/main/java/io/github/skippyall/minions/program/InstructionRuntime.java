package io.github.skippyall.minions.program;

import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ExecutingInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.server.MinecraftServer;

import java.util.OptionalInt;

public interface InstructionRuntime {
    boolean isInstructionEnabled(InstructionType type);

    int addInstruction(ExecutingInstruction executingInstruction);

    MinecraftServer getServer();

    ExecutionContext getContext();

    OptionalInt run(ConfiguredInstruction instruction);
}
