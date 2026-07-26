package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.gui.input.BooleanInput;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.program.instruction.move.TurnDirection;
import io.github.skippyall.minions.program.value.SimpleValueType;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

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
                            oldValue
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
                            oldValue
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
                    (parent, value) -> BooleanInput.confirm(parent, Component.literal(""), Component.translatable("value_type.minions.boolean.false"), Component.translatable("value_type.minions.boolean.true")),
                    value -> Component.literal(value.toString())
            )
    );

    @SuppressWarnings("Convert2Diamond")
    public static ValueType<String> STRING = register(
            "string",
            new SimpleValueType<String>(
                    Codec.STRING,
                    "",
                    o -> o instanceof String s ? s : null,
                    ((parent, oldValue) -> TextInput.inputString(
                            parent,
                            Component.literal("Text"),
                            oldValue
                    )),
                    value -> Component.literal("\"" + value + "\"")
            )
    );

    @SuppressWarnings("Convert2Diamond")
    public static ValueType<TurnDirection> TURN_DIRECTION = register(
            "turn_direction",
            new SimpleValueType<TurnDirection>(
                    TurnDirection.CODEC,
                    TurnDirection.RIGHT,
                    o -> o instanceof TurnDirection d ? d : null,
                    (parent, oldValue) -> PaginatedList.createListFuture(
                            parent,
                            Component.translatable("value_type.minions.turn_direction"),
                            List.of(TurnDirection.values()),
                            value -> new GuiElementBuilder(value.getDisplay().createItemStack())
                    ),
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
