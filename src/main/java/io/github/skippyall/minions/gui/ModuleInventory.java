package io.github.skippyall.minions.gui;

import io.github.skippyall.minions.command.Command;
import io.github.skippyall.minions.module.ModuleItem;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;

public class ModuleInventory extends SimpleInventory {
    public ModuleInventory() {
        super(27);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return (stack.getCount() <= getMaxCountPerStack()) && stack.getItem() instanceof ModuleItem;
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        Inventories.readNbt(nbt, heldStacks, lookup);
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        return Inventories.writeNbt(nbt, heldStacks, lookup);
    }

    public boolean hasModule(ModuleItem module) {
        for(ItemStack stack : heldStacks) {
            if(stack.getItem() instanceof ModuleItem module2 && module2 == module) {
                return true;
            }
        }
        return false;
    }

    public List<ModuleItem> getModuleItems() {
        ArrayList<ModuleItem> modules = new ArrayList<>();
        for(ItemStack stack : heldStacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                modules.add(module);
            }
        }
        return modules;
    }

    public List<Command> getAllCommands() {
        ArrayList<Command> commands = new ArrayList<>();
        for(ItemStack stack : heldStacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                commands.addAll(module.getCommands());
            }
        }
        return commands;
    }

    public List<CodeBlock<?,?>> getAllCodeBlocks() {
        ArrayList<CodeBlock<?,?>> commands = new ArrayList<>();
        for(ItemStack stack : heldStacks) {
            if(stack.getItem() instanceof ModuleItem module) {
                commands.addAll(module.getCodeBlocks());
            }
        }
        return commands;
    }
}
