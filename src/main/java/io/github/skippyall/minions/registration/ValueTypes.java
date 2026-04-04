package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.value.SimpleValueType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.input.ChoiceInput;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.program.instruction.move.TurnDirection;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ValueTypes {
    public static ValueType<Long> LONG = register(
            "long",
            new SimpleValueType<>(
                    Codec.LONG,
                    0L,
                    o -> o instanceof Long l ? l : null,
                    (player, oldValue) -> TextInput.inputLong(
                            player,
                            Text.literal("Integer"),
                            String.valueOf(oldValue)
                    ),
                    value -> Text.literal(value.toString())
            )
    );

    public static ValueType<Double> DOUBLE = register(
            "double",
            new SimpleValueType<>(
                    Codec.DOUBLE,
                    0D,
                    o -> o instanceof Double d ? d : null,
                    (player, oldValue) -> TextInput.inputDouble(
                            player,
                            Text.literal("Number"),
                            String.valueOf(oldValue)
                    ),
                    value -> Text.literal(value.toString())
            )
    );

    public static ValueType<Boolean> BOOLEAN = register(
            "boolean",
            new SimpleValueType<>(
                    Codec.BOOL,
                    false,
                    o -> o instanceof Boolean b ? b : null,
                    ChoiceInput.inputBoolean(Text.literal("")),
                    value -> Text.literal(value.toString())
            )
    );

    public static ValueType<String> STRING = register(
            "string",
            new SimpleValueType<>(
                    Codec.STRING,
                    "",
                    o -> o instanceof String s ? s : null,
                    ((player, oldValue) -> TextInput.inputString(
                            player,
                            Text.literal("Text"),
                            oldValue)
                    ),
                    value -> Text.literal("\"" + value + "\"")
            )
    );

    public static ValueType<TurnDirection> TURN_DIRECTION = register(
            "turn_direction",
            new SimpleValueType<>(
                    TurnDirection.CODEC,
                    TurnDirection.RIGHT,
                    o -> o instanceof TurnDirection d ? d : null,
                    ChoiceInput.createDialogOpener(TurnDirection.values()),
                    value -> Text.literal(value.name)
            )
    );

    private static <T extends ValueType<?>> T register(
            String id,
            T type
    ) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        Registry.register(
                MinionRegistries.VALUE_TYPES,
                identifier,
                type
        );
        return type;
    }

    public static void register() {}
}
