package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.input.ChoiceInput;
import io.github.skippyall.minions.input.TextInput;
import io.github.skippyall.minions.program.instruction.execution.TurnExecution;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ValueTypes {
    public static ValueType<Integer> INTEGER = registerSimple(
            "integer",
            Codec.INT,
            key -> new GuiDisplay.ModelBased(Items.NETHERITE_SCRAP, key, true),
            0,
            (player, oldValue) -> TextInput.inputInt(
                    player,
                    Text.literal("Integer"),
                    String.valueOf(oldValue)
            )
    );

    public static ValueType<Float> FLOAT = registerSimple(
            "float",
            Codec.FLOAT,
            key -> new GuiDisplay.ModelBased(Items.BAMBOO_RAFT, key, true),
            0F,
            (player, oldValue) -> TextInput.inputFloat(
                    player,
                    Text.literal("Number"),
                    String.valueOf(oldValue)
            )
    );

    public static ValueType<String> STRING = registerSimple(
            "string",
            Codec.STRING,
            key -> new GuiDisplay.ModelBased(Items.STRING, key, true),
            "",
            ((player, oldValue) -> TextInput.inputString(
                    player,
                    Text.literal("Text"),
                    oldValue)
            )
    );

    public static ValueType<TurnExecution.TurnDirection> TURN_DIRECTION = registerSimple(
            "turn_direction",
            TurnExecution.TurnDirection.CODEC,
            base -> new GuiDisplay.ModelBased(Items.STRUCTURE_VOID, base, true),
            TurnExecution.TurnDirection.RIGHT,
            ChoiceInput.createDialogOpener(TurnExecution.TurnDirection.values())
    );

    public static ValueType<Void> VOID = registerSimple(
            "void",
            Codec.unit(null),
            key -> new GuiDisplay.ModelBased(Items.BARRIER, key, false),
            null,
            (player, oldValue) -> CompletableFuture.completedFuture(null)
    );

    private static <T> ValueType<T> registerSimple(String id, Codec<T> codec, Function<String, GuiDisplay> displayFunction, T defaultValue, BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> valueDialogOpener) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(
                MinionRegistries.VALUE_TYPES,
                identifier,
                new ValueType<>(
                        codec,
                        displayFunction.apply(identifier.toTranslationKey("value_type")),
                        defaultValue,
                        valueDialogOpener
                )
        );
    }

    public static void register() {}
}
