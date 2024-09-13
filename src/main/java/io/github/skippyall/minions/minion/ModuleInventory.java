package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.module.ModuleItem;
import io.github.skippyall.minions.module.Modules;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class ModuleInventory implements ImplementedInventory {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public ModuleInventory() {
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return (stack.getCount() <= getMaxCountPerStack()) && stack.getItem() instanceof ModuleItem;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        Inventories.readNbt(nbt, stacks, lookup);
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        return Inventories.writeNbt(nbt, stacks, lookup);
    }

    public List<ModuleItem> getModuleItems() {
        ArrayList<ModuleItem> modules = new ArrayList<>();
        for(ItemStack stack : stacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                modules.add(module);
            }
        }
        return modules;
    }

    public List<Command> getAllCommands() {
        ArrayList<Command> commands = new ArrayList<>();
        for(ItemStack stack : stacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                commands.addAll(module.getCommands());
            }
        }
        return commands;
    }

    public List<CodeBlock<?,?>> getAllCodeBlocks() {
        ArrayList<CodeBlock<?,?>> commands = new ArrayList<>();
        for(ItemStack stack : stacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                commands.addAll(module.getCodeBlocks());
            }
        }
        return commands;
    }
}
