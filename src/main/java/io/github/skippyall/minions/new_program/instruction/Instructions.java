package io.github.skippyall.minions.new_program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.new_program.instruction.execution.WalkExecution;
import io.github.skippyall.minions.new_program.argument.Parameter;
import io.github.skippyall.minions.new_program.value.ValueType;
import io.github.skippyall.minions.new_program.value.ValueTypes;
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
            base -> new GuiDisplay.ModelBased(ModelIdUtil.getItemModelId(Items.IRON_PICKAXE), base, true),
            WalkExecution::new,
            ValueTypes.VOID,
            WalkExecution.blocksToMoveParam
    );

    private static <R> InstructionType<R> register(String id, Function<String, GuiDisplay> displayFunction, Supplier<InstructionExecution<R>> factory, ValueType<R> returnType, Parameter<?>... parameters) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, InstructionType.create(displayFunction.apply(identifier.toTranslationKey("instruction_type")), factory, returnType, parameters));
    }

    public static void register() {

    }
}
