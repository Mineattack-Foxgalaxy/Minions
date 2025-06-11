package io.github.skippyall.minions.module.instruction;

import io.github.skippyall.minions.new_program.instruction.Instruction;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

public record InstructionDisplay(Text name, Text description, Item itemRepresentation, Instruction instruction) {

}
