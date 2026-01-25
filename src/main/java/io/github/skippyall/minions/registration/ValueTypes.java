package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.input.ChoiceInput;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.instruction.move.TurnDirection;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class ValueTypes {
    public static ValueType<Long> LONG = registerSimple(
            "long",
            Codec.LONG,
            0L,
            (player, oldValue) -> TextInput.inputLong(
                    player,
                    Text.literal("Integer"),
                    String.valueOf(oldValue)
            )
    );

    public static ValueType<Double> DOUBLE = registerSimple(
            "double",
            Codec.DOUBLE,
            0D,
            (player, oldValue) -> TextInput.inputDouble(
                    player,
                    Text.literal("Number"),
                    String.valueOf(oldValue)
            )
    );

    public static ValueType<String> STRING = registerSimple(
            "string",
            Codec.STRING,
            "",
            ((player, oldValue) -> TextInput.inputString(
                    player,
                    Text.literal("Text"),
                    oldValue)
            )
    );

    public static ValueType<TurnDirection> TURN_DIRECTION = registerSimple(
            "turn_direction",
            TurnDirection.CODEC,
            TurnDirection.RIGHT,
            ChoiceInput.createDialogOpener(TurnDirection.values())
    );

    private static <T> ValueType<T> registerSimple(String id, Codec<T> codec, T defaultValue, BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> valueDialogOpener) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(
                MinionRegistries.VALUE_TYPES,
                identifier,
                new ValueType<>(
                        codec,
                        defaultValue,
                        valueDialogOpener
                )
        );
    }

    public static void register() {}
}
