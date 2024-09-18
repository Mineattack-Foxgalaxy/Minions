package io.github.skippyall.minions.command;

import net.minecraft.item.Item;
import net.minecraft.text.Text;

public interface Command extends CommandExecutor {
    Text getName();
    Text getDescription();
    Item getItemRepresentation();
}
