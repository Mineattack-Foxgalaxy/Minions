package io.github.skippyall.minions.module;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleModuleItem extends Item implements PolymerItem, ModuleItem {
    private final List<CodeBlock<?,?>> codeBlocks;
    private final List<Command> commands;
    private final Item vanillaItem;

    public SimpleModuleItem(List<CodeBlock<?,?>> codeBlocks, List<Command> commands, Item vanillaItem) {
        super(new Item.Settings().maxCount(1));
        this.codeBlocks = codeBlocks;
        this.commands = commands;
        this.vanillaItem = vanillaItem;
    }

    @Override
    public List<CodeBlock<?, ?>> getCodeBlocks() {
        return codeBlocks;
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return vanillaItem;
    }
}
