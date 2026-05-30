package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;

public class ConverterList {
    public static final Codec<ConverterList> CODEC = ValueConverter.CODEC.listOf().xmap(ConverterList::new, l -> l.converters);

    private final List<ValueConverter<?,?>> converters;

    public ConverterList() {
        this.converters = new ArrayList<>();
    }

    private ConverterList(List<ValueConverter<?,?>> converters) {
        this.converters = new ArrayList<>(converters);
    }

    public static ConverterList of(List<ValueConverter<?,?>> converters) {
        return new ConverterList(new ArrayList<>(converters));
    }

    public List<ValueConverter<?,?>> getConverters() {
        return converters;
    }

    public ValueType<?> getInputType() {
        return converters.getFirst().getFrom();
    }

    public ValueType<?> getOutputType() {
        return converters.getLast().getTo();
    }

    public Result<TypedValue<?>, Component> convert(TypedValue<?> input) {
        if(converters.isEmpty()) {
            return new Result.Success<>(input);
        } else {
            ListIterator<ValueConverter<?, ?>> iterator = converters.listIterator();
            return convert(input, iterator.next(), iterator);
        }
    }

    private <F,I,T> Result<TypedValue<?>, Component> convert(TypedValue<F> from, ValueConverter<I,T> converter, ListIterator<ValueConverter<?,?>> iterator) {
        Result<I, Component> inter = Casts.castOrError(from, converter.getFrom());
        if(inter instanceof Result.Error<I, Component> error) {
            return new Result.Error<>(
                    Component.translatable("minions.converter.list.passing_error", iterator.previousIndex())
                            .append("\n")
                            .append(error.message())
            );
        }
        Result<T, Component> to = converter.convert(inter.getOrThrow());

        if(iterator.hasNext() && to instanceof Result.Success<T, Component> success) {
            return convert(new TypedValue<>(success.result(), converter.getTo()), iterator.next(), iterator);
        } else {
            return to.map(v -> new TypedValue<>(v, converter.getTo()));
        }
    }

    public static @Nullable Component createConverterWarning(ValueConverter<?, ?> converter) {
        return null;
    }

    public static @Nullable Component createCastWarning(ValueType<?> fromType, ValueType<?> toType) {
        Component warning = null;

        Cast<?,?> cast = Casts.getCast(fromType, toType);
        if(cast == null) {
            warning = Component.translatable("minions.converter.cast.not_found", TranslationUtil.getTranslation(fromType, MinionRegistries.VALUE_TYPES), TranslationUtil.getTranslation(toType, MinionRegistries.VALUE_TYPES));
        }

        return warning;
    }

    public void check(Consumer<Component> errorConsumer, ValueType<?> input, ValueType<?> output) {
        Component firstCastWarning = createCastWarning(input, converters.isEmpty() ? output : converters.get(0).getFrom());
        if(firstCastWarning != null) {
            errorConsumer.accept(firstCastWarning);
        }
        for(int i = 0; i < converters.size(); i++) {
            ValueConverter<?,?> converter = converters.get(i);
            Component converterWarning = createConverterWarning(converter);
            if(converterWarning != null) {
                errorConsumer.accept(converterWarning);
            }
            Component castWarning = createCastWarning(converter.getTo(), i + 1 < converters.size() ? converters.get(i + 1).getFrom() : output);
            if(castWarning != null) {
                errorConsumer.accept(castWarning);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConverterList that)) return false;
        return Objects.equals(converters, that.converters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(converters);
    }
}
