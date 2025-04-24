package io.github.skippyall.minions.module;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import io.github.skippyall.minions.module.command.Command;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.item.Item;

import java.util.List;

public class SimpleModuleItem extends SimplePolymerItem implements ModuleItem {
    private final List<CodeBlock<?,?>> codeBlocks;
    private final List<Command> commands;

    public SimpleModuleItem(List<CodeBlock<?,?>> codeBlocks, List<Command> commands, Settings settings, Item vanillaItem) {
        super(settings.maxCount(1), vanillaItem);
        this.codeBlocks = codeBlocks;
        this.commands = commands;
    }

    @Override
    public List<CodeBlock<?, ?>> getCodeBlocks() {
        return codeBlocks;
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
