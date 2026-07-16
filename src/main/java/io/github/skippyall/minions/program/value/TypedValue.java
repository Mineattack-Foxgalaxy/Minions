package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public record TypedValue<T>(T value, ValueType<T> type) {
    public static final Codec<TypedValue<?>> CODEC = MinionRegistries.VALUE_TYPES.byNameCodec().dispatch(
            TypedValue::type,
            TypedValue::codecHelper
    );

    private static <T> MapCodec<TypedValue<T>> codecHelper(ValueType<T> type) {
        return type.codec()
                .xmap(
                        value -> new TypedValue<>(value, type),
                        tv -> tv.value
                )
                .fieldOf("value");
    }

    public static <T> TypedValue<T> of(Object o, ValueType<T> type) {
        return new TypedValue<T>(Objects.requireNonNull(type.checkedCast(o)), type);
    }

    public Component getDisplayText() {
        return type.getDisplayText(value);
    }
}
