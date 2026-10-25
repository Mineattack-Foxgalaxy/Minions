package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;

public interface ValueConverter {
    Codec<ValueConverter> CODEC = MinionRegistries.VALUE_CONVERTER_TYPES.byNameCodec().dispatch(ValueConverter::getType, ValueConverterType::getCodec);

    Result<? extends TypedValue<?>, Component> convert(TypedValue<?> from);

    ValueType<?> getOutputType(ValueType<?> inputType);

    ValueConverterType<?> getType();

    Component getDisplayText();
}
