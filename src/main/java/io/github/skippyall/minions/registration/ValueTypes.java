package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.program.instruction.move.TurnDirection;
import io.github.skippyall.minions.program.value.SimpleValueType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ValueTypes {
    public static ValueType<Long> LONG = register(
            "long",
            new SimpleValueType<>(
                    Codec.LONG,
                    0L,
                    o -> o instanceof Long l ? l : null,
                    (parent, oldValue) -> TextInput.inputLong(
                            parent,
                            Component.literal("Integer"),
                            String.valueOf(oldValue)
                    ),
                    value -> Component.literal(value.toString())
            )
    );

    public static ValueType<Double> DOUBLE = register(
            "double",
            new SimpleValueType<>(
                    Codec.DOUBLE,
                    0D,
                    o -> o instanceof Double d ? d : null,
                    (parent, oldValue) -> TextInput.inputDouble(
                            parent,
                            Component.literal("Number"),
                            String.valueOf(oldValue)
                    ),
                    value -> Component.literal(value.toString())
            )
    );

    public static ValueType<Boolean> BOOLEAN = register(
            "boolean",
            new SimpleValueType<>(
                    Codec.BOOL,
                    false,
                    o -> o instanceof Boolean b ? b : null,
                    //TODO Properly implement ChoiceInput
                    (gui, value) -> CompletableFuture.completedFuture(value),//ChoiceInput.inputBoolean(Text.literal("")),
                    value -> Component.literal(value.toString())
            )
    );

    public static ValueType<String> STRING = register(
            "string",
            new SimpleValueType<>(
                    Codec.STRING,
                    "",
                    o -> o instanceof String s ? s : null,
                    ((parent, oldValue) -> TextInput.inputString(
                            parent,
                            Component.literal("Text"),
                            oldValue)
                    ),
                    value -> Component.literal("\"" + value + "\"")
            )
    );

    public static ValueType<TurnDirection> TURN_DIRECTION = register(
            "turn_direction",
            new SimpleValueType<>(
                    TurnDirection.CODEC,
                    TurnDirection.RIGHT,
                    o -> o instanceof TurnDirection d ? d : null,
                    //TODO Properly implement ChoiceInput
                    (parent, oldValue) -> CompletableFuture.completedFuture(oldValue), // ChoiceInput.createDialogOpener(TurnDirection.values()),
                    value -> Component.literal(value.name)
            )
    );

    private static <T extends ValueType<?>> T register(
            String id,
            T type
    ) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Minions.MOD_ID, id);
        Registry.register(
                MinionRegistries.VALUE_TYPES,
                identifier,
                type
        );
        return type;
    }

    public static void register() {}
}
