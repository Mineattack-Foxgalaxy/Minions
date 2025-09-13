package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.program.instruction.execution.ActionExecution;
import io.github.skippyall.minions.program.instruction.execution.WalkExecution;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.program.value.ValueTypes;
import io.github.skippyall.minions.util.ModelIdUtil;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

public class Instructions {
    public static final InstructionType<Void> WALK = register(
            "walk",
            base -> new GuiDisplay.ModelBased(ModelIdUtil.getItemModelId(Items.IRON_BOOTS), base, true),
            WalkExecution::new,
            ValueTypes.VOID,
            WalkExecution.blocksToMoveParam
    );

    public static final InstructionType<Void> ATTACK = register(
            "attack",
            base -> new GuiDisplay.ModelBased(ModelIdUtil.getItemModelId(Items.IRON_BOOTS), base, true),
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK),
            ValueTypes.VOID
    );

    public static final InstructionType<Void> USE = register(
            "use",
            base -> new GuiDisplay.ModelBased(ModelIdUtil.getItemModelId(Items.LEVER), base, true),
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.USE),
            ValueTypes.VOID
    );

    private static <R> InstructionType<R> register(String id, Function<String, GuiDisplay> displayFunction, Supplier<InstructionExecution<R>> factory, ValueType<R> returnType, Parameter<?>... parameters) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, InstructionType.create(displayFunction.apply(identifier.toTranslationKey("instruction_type")), factory, returnType, parameters));
    }

    public static void register() {

    }
}
