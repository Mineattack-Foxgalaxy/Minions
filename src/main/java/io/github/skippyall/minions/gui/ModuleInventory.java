package io.github.skippyall.minions.gui;

import io.github.skippyall.minions.module.command.Command;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.ModuleItem;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.fabricmc.fabric.impl.transfer.item.ComposterWrapper;
import net.fabricmc.fabric.mixin.transfer.JukeboxBlockEntityMixin;
import net.minecraft.block.ComposterBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleInventory extends SimpleInventory {
    private final Set<ModuleItem> modules = new HashSet<>();
    public ModuleInventory() {
        super(27);
    }

    public static void openModuleInventory(ServerPlayerEntity player, MinionFakePlayer minion) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player2) -> new ModuleInventoryScreenHandler(syncId, playerInventory, minion.getModuleInventory()), Text.translatable("minions.gui.modules.title", minion.getName())));
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
    public void markDirty() {
        super.markDirty();
        updateModules();
    }

    public void updateModules() {
        modules.clear();
        for (ItemStack heldStack : heldStacks) {
            if(heldStack.getItem() instanceof ModuleItem moduleItem) {
                modules.add(moduleItem);
            }
        }
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        Inventories.readNbt(nbt, heldStacks, lookup);
        updateModules();
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        return Inventories.writeNbt(nbt, heldStacks, lookup);
    }

    public boolean hasModule(ModuleItem module) {
        return modules.contains(module);
    }

    public Collection<ModuleItem> getModuleItems() {
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
