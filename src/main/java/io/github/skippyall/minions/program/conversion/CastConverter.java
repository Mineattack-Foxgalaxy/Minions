package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueConverters;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CastConverter<F,T> implements ValueConverter<F,T> {
    private static final MapCodec<CastConverter<?,?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MinionRegistries.VALUE_TYPES.getCodec().fieldOf("from").forGetter(CastConverter::getFrom),
                    MinionRegistries.VALUE_TYPES.getCodec().fieldOf("to").forGetter(CastConverter::getTo)
            ).apply(instance, CastConverter::new)
    );

    private final ValueType<F> from;
    private final ValueType<T> to;

    public CastConverter(ValueType<F> from, ValueType<T> to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public T convert(F fromValue) {
        return Casts.getCast(from, to).cast(fromValue);
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
        public <F,T> CompletableFuture<CastConverter<?,?>> configure(ServerPlayerEntity player, ValueType<F> from, ValueType<T> to, @Nullable CastConverter<?, ?> old) {
            return CompletableFuture.completedFuture(new CastConverter<>(from, to));
        }
    }
}
