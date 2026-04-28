package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

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

    public Result<TypedValue<?>, Text> convert(TypedValue<?> input) {
        if(converters.isEmpty()) {
            return new Result.Success<>(input);
        } else {
            ListIterator<ValueConverter<?, ?>> iterator = converters.listIterator();
            return convert(input, iterator.next(), iterator);
        }
    }

    private <F,I,T> Result<TypedValue<?>, Text> convert(TypedValue<F> from, ValueConverter<I,T> converter, ListIterator<ValueConverter<?,?>> iterator) {
        Result<I, Text> inter = Casts.castOrError(from, converter.getFrom());
        if(inter instanceof Result.Error<I, Text> error) {
            return new Result.Error<>(Text.translatable("minions.converter.list.passing_error", iterator.previousIndex(), error.message()));
        }
        Result<T, Text> to = converter.convert(inter.getOrThrow());

        if(iterator.hasNext() && to instanceof Result.Success<T, Text> success) {
            return convert(new TypedValue<>(success.result(), converter.getTo()), iterator.next(), iterator);
        } else {
            return to.map(v -> new TypedValue<>(v, converter.getTo()));
        }
    }

    public Result<@Nullable Void, Text> check() {
        return new Result.Success<>(null);
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
