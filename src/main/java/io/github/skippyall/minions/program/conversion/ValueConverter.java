package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.text.Text;

public interface ValueConverter<F,T> {
    Codec<ValueConverter<?,?>> CODEC = MinionRegistries.VALUE_CONVERTER_TYPES.getCodec().dispatch(ValueConverter::getType, ValueConverterType::getCodec);

    T convert(F from);

    ValueType<F> getFrom();

    ValueType<T> getTo();

    ValueConverterType<?> getType();

    Text getDisplayText();

    default <F2,T2> ValueConverter<F2,T2> cast(ValueType<F2> from, ValueType<T2> to) {
        if(from == getFrom() && to == getTo()) {
            //noinspection unchecked
            return (ValueConverter<F2, T2>) this;
        } else {
            return null;
        }
    }
}
