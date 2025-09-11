package io.github.skippyall.minions.gui;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_module.MinionModule;
import io.github.skippyall.minions.new_program.instruction.InstructionType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleInventory extends SimpleInventory {
    private final Set<MinionModule> modules = new HashSet<>();
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
        return (stack.getCount() <= getMaxCountPerStack()) && stack.contains(MinionModule.COMPONENT_TYPE);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        updateModules();
    }

    public void updateModules() {
        modules.clear();
        for (ItemStack heldStack : heldStacks) {
            MinionModule module = heldStack.get(MinionModule.COMPONENT_TYPE);
            if(module != null) {
                modules.add(module);
            }
        }
    }

    public void readData(ReadView view) {
        Inventories.readData(view, heldStacks);
        updateModules();
    }

    public void writeData(WriteView view) {
        Inventories.writeData(view, heldStacks);
    }

    public boolean hasModule(MinionModule module) {
        return modules.contains(module);
    }

    public Collection<MinionModule> getModules() {
        return modules;
    }

    public List<InstructionType<?>> getAllInstructions() {
        ArrayList<InstructionType<?>> instructionTypes = new ArrayList<>();
        for(MinionModule module : modules) {
            instructionTypes.addAll(module.instructions());
        }
        return instructionTypes;
    }
}
