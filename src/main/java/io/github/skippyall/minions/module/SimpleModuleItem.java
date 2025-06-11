package io.github.skippyall.minions.module;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import io.github.skippyall.minions.module.command.Command;
import io.github.skippyall.minions.module.instruction.InstructionDisplay;
import io.github.skippyall.minions.new_program.instruction.Instruction;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.item.Item;

import java.util.List;

public class SimpleModuleItem extends SimplePolymerItem implements ModuleItem {
    private final List<InstructionDisplay> instructions;

    public SimpleModuleItem(List<InstructionDisplay> instructions, Settings settings, Item vanillaItem) {
        super(settings.maxCount(1), vanillaItem);
        this.instructions = instructions;
    }

    @Override
    public List<InstructionDisplay> getInstructions() {
        return instructions;
    }
}
