package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CastConverter<F,T> implements ValueConverter<F,T> {
    private static final MapCodec<CastConverter<?,?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("from").forGetter(CastConverter::getFrom),
                    MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("to").forGetter(CastConverter::getTo)
            ).apply(instance, CastConverter::new)
    );

    private final ValueType<F> from;
    private final ValueType<T> to;

    public CastConverter(ValueType<F> from, ValueType<T> to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Result<T, Component> convert(F fromValue) {
        return Casts.castOrError(new TypedValue<>(fromValue, from), to);
    }

    @Override
    public ValueType<F> getFrom() {
        return from;
    }

    @Override
    public ValueType<T> getTo() {
        return to;
    }

    @Override
    public ValueConverterType<?> getType() {
        return ValueConverters.CAST_CONVERTER;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_converter.minions.cast.display", Component.translatable(TranslationUtil.getTranslationKey(from, MinionRegistries.VALUE_TYPES)), Component.translatable(TranslationUtil.getTranslationKey(to, MinionRegistries.VALUE_TYPES)));
    }

    public static class Type implements ValueConverterType<CastConverter<?,?>> {
        @Override
        public MapCodec<CastConverter<?, ?>> getCodec() {
            return CastConverter.CODEC;
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from, ValueType<?> to) {
            return Casts.getCast(from, to) != null;
        }

        @Override
        public <F,T> CompletableFuture<CastConverter<?,?>> configure(MinionsGui parent, ValueType<F> from, ValueType<T> to, @Nullable ValueConverter<?, ?> old) {
            return CompletableFuture.completedFuture(new CastConverter<>(from, to));
        }
    }
}
