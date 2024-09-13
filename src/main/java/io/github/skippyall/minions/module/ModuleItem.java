package io.github.skippyall.minions.module;

import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.text.Text;

import java.util.List;

public interface ModuleItem {

    List<CodeBlock<?,?>> getCodeBlocks();

    List<Command> getCommands();
}
