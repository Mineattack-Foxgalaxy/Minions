package io.github.skippyall.minions.module;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ModuleInventory extends SimpleContainer {
    private final Set<MinionModule> modules = new HashSet<>();
    private final Set<InstructionType<MinionRuntime>> instructions = new HashSet<>();
    private final Set<SpecialAbility> specialAbilities = new HashSet<>();

    private final MinionFakePlayer minion;

    public ModuleInventory(MinionFakePlayer minion) {
        super(27);
        this.minion = minion;
    }

    public static void openModuleInventory(ServerPlayer player, MinionFakePlayer minion) {
        player.openMenu(new SimpleMenuProvider((syncId, playerInventory, player2) -> new ModuleInventoryScreenHandler(syncId, playerInventory, minion.getModuleInventory()), Component.translatable("minions.gui.modules.title", minion.getName())));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return (stack.getCount() <= getMaxStackSize()) && stack.has(MinionComponentTypes.MODULE);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateModules();
    }

    public void updateModules() {
        Set<MinionModule> oldModules = Set.copyOf(modules);
        Set<InstructionType<MinionRuntime>> oldInstructions = Set.copyOf(instructions);
        Set<SpecialAbility> oldAbilities = Set.copyOf(specialAbilities);

        modules.clear();
        instructions.clear();
        specialAbilities.clear();
        for (ItemStack heldStack : items) {
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

    public void readData(ValueInput view) {
        ContainerHelper.loadAllItems(view, items);
        updateModules();
    }

    public void writeData(ValueOutput view) {
        ContainerHelper.saveAllItems(view, items);
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
