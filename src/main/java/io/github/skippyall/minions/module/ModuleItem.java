package io.github.skippyall.minions.module;

import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.item.ItemConvertible;

import java.util.List;

public interface ModuleItem extends ItemConvertible {
    List<CodeBlock<?,?>> getCodeBlocks();

    List<Command> getCommands();
}
