package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.program.instruction.execution.ActionExecution;
import io.github.skippyall.minions.program.instruction.execution.TurnExecution;
import io.github.skippyall.minions.program.instruction.execution.WalkExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Instructions {
    public static final InstructionType<MinionRuntime> WALK = register(
            "walk",
            base -> new GuiDisplay.ModelBased(Items.IRON_BOOTS, base, true),
            WalkExecution::new,
            List.of(WalkExecution.blocksToMoveParam)
    );

    public static final InstructionType<MinionRuntime> TURN = register(
            "turn",
            base -> new GuiDisplay.ModelBased(Items.STRUCTURE_VOID, base, true),
            TurnExecution::new,
            List.of(TurnExecution.ANGLE, TurnExecution.DIRECTION)
    );

    public static final InstructionType<MinionRuntime> ATTACK = register(
            "attack",
            base -> new GuiDisplay.ModelBased(Items.IRON_PICKAXE, base, true),
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK)
    );

    public static final InstructionType<MinionRuntime> USE = register(
            "use",
            base -> new GuiDisplay.ModelBased(Items.LEVER, base, true),
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.USE)
    );

    private static InstructionType<MinionRuntime> register(String id, Function<String, GuiDisplay> displayFunction, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, new InstructionType<>(displayFunction.apply(identifier.toTranslationKey("instruction_type")), factory, parameters, returnParameters));
    }

    private static InstructionType<MinionRuntime> register(String id, Function<String, GuiDisplay> displayFunction, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters) {
        return register(id, displayFunction, factory, parameters, List.of());
    }

    private static InstructionType<MinionRuntime> register(String id, Function<String, GuiDisplay> displayFunction, Supplier<InstructionExecution<MinionRuntime>> factory) {
        return register(id, displayFunction, factory, List.of(), List.of());
    }

    public static void register() {

    }
}
