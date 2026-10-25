package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConverterList extends AbstractList<ValueConverter> {
    public static final Codec<ConverterList> CODEC = ValueConverter.CODEC.listOf().xmap(ConverterList::new, l -> l.converters);

    private final List<ValueConverter> converters;

    public ConverterList() {
        this.converters = new ArrayList<>();
    }

    private ConverterList(List<ValueConverter> converters) {
        this.converters = new ArrayList<>(converters);
    }

    public static ConverterList of(List<ValueConverter> converters) {
        return new ConverterList(new ArrayList<>(converters));
    }

    public Result<? extends TypedValue<?>, Component> convert(TypedValue<?> input) {
        TypedValue<?> current = input;
        for(ValueConverter converter : converters) {
            Result<? extends TypedValue<?>, Component> result = converter.convert(current);
            if(result instanceof Result.Success<? extends TypedValue<?>, Component> success) {
                current = success.result();
            } else if (result instanceof Result.Error<? extends TypedValue<?>, Component> error){
                return error;
            }
        }
        return new Result.Success<>(current);
    }

    public static @Nullable Component createConverterWarning(ValueConverter converter) {
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
        for(ValueConverter converter : converters) {
            Component converterWarning = createConverterWarning(converter);
            if(converterWarning != null) {
                errorConsumer.accept(converterWarning);
            }
        }
    }

    @Override
    public int size() {
        return converters.size();
    }

    @Override
    public ValueConverter get(int index) {
        return converters.get(index);
    }

    @Override
    public ValueConverter set(int index, ValueConverter element) {
        return converters.set(index, element);
    }

    @Override
    public void add(int index, ValueConverter valueConverter) {
        converters.add(index, valueConverter);
    }

    @Override
    public ValueConverter remove(int index) {
        return converters.remove(index);
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
