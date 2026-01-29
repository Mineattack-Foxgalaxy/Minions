package io.github.skippyall.minions.module;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ModuleInventory extends SimpleInventory {
    private final Set<MinionModule> modules = new HashSet<>();
    private final Set<InstructionType<MinionRuntime>> instructions = new HashSet<>();
    private final Set<SpecialAbility> specialAbilities = new HashSet<>();

    private final MinionFakePlayer minion;

    public ModuleInventory(MinionFakePlayer minion) {
        super(27);
        this.minion = minion;
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
        return (stack.getCount() <= getMaxCountPerStack()) && stack.contains(MinionComponentTypes.MODULE);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        updateModules();
    }

    public void updateModules() {
        Set<MinionModule> oldModules = Set.copyOf(modules);
        Set<InstructionType<MinionRuntime>> oldInstructions = Set.copyOf(instructions);
        Set<SpecialAbility> oldAbilities = Set.copyOf(specialAbilities);

        modules.clear();
        instructions.clear();
        specialAbilities.clear();
        for (ItemStack heldStack : heldStacks) {
            MinionModule module = heldStack.get(MinionComponentTypes.MODULE);
            if(module != null) {
                modules.add(module);
                instructions.addAll(module.instructions());
                specialAbilities.addAll(module.specialAbilities());

                for(InstructionType<MinionRuntime> instructionType : module.instructions()) {
                    if(!oldInstructions.contains(instructionType)) {
                        minion.getInstructionManager().enableInstructionType(instructionType);
                    }
                }

                for(SpecialAbility ability : module.specialAbilities()) {
                    if(!oldAbilities.contains(ability)) {
                        ability.onAdd(minion);
                    }
                }
            }
        }

        for(InstructionType<MinionRuntime> instructionType : oldInstructions) {
            if(!instructions.contains(instructionType)) {
                minion.getInstructionManager().disableInstructionType(instructionType);
            }
        }

        for(SpecialAbility ability : oldAbilities) {
            if(!specialAbilities.contains(ability)) {
                ability.onRemove(minion);
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

    public Collection<MinionModule> getModules() {
        return modules;
    }

    public boolean hasAbility(SpecialAbility ability) {
        return specialAbilities.contains(ability);
    }

    public boolean hasInstruction(InstructionType<MinionRuntime> instructionType) {
        return instructions.contains(instructionType);
    }

    public Collection<InstructionType<MinionRuntime>> getAllInstructions() {
        return instructions;
    }
}
