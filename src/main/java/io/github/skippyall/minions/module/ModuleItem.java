package io.github.skippyall.minions.module;

import io.github.skippyall.minions.module.instruction.InstructionDisplay;
import net.minecraft.item.ItemConvertible;

import java.util.List;

public interface ModuleItem extends ItemConvertible {
    List<InstructionDisplay> getInstructions();
}
