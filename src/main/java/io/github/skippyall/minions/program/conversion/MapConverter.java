package io.github.skippyall.minions.program.conversion;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.converter.MapConverterGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.util.CodecUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapConverter<F,T> implements ValueConverter<F,T> {
    public static final MapCodec<MapConverter<?,?>> CODEC = Codec.mapPair(
            MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("fromType"),
            MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("toType")
    ).dispatchMap(
            converter -> new Pair<>(converter.getFrom(), converter.getTo()),
            types -> codecHelper(types.getFirst(), types.getSecond())
    );

    private static <F, T> MapCodec<MapConverter<F, T>> codecHelper(ValueType<F> from, ValueType<T> to) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        CodecUtil.createMapAsListCodec(
                                from.codec(),
                                to.codec(),
                                "from",
                                "to"
                        ).fieldOf("map").forGetter(MapConverter::getMap),
                        to.codec().fieldOf("defaultValue").forGetter(MapConverter::getDefaultValue)
                ).apply(instance, (map, defaultValue) -> new MapConverter<>(from, to, map, defaultValue))
        );
    }

    private final ValueType<F> from;
    private final ValueType<T> to;

    private final Map<F, T> map;
    private final T defaultValue;

    public MapConverter(ValueType<F> from, ValueType<T> to, Map<F, T> map, T defaultValue) {
        this.from = from;
        this.to = to;
        this.map = map;
        this.defaultValue = defaultValue;
    }

    @Override
    public Result<T, Component> convert(F from) {
        return new Result.Success<>(map.getOrDefault(from, defaultValue));
    }

    @Override
    public ValueType<F> getFrom() {
        return from;
    }

    @Override
    public ValueType<T> getTo() {
        return to;
    }

    public Map<F, T> getMap() {
        return map;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public ValueConverterType<?> getType() {
        return ValueConverters.MAP_CONVERTER;
    }

    @Override
    public Component getDisplayText() {
        MutableComponent map = Component.empty();

        boolean first = true;
        for(Map.Entry<F, T> entry : this.map.entrySet()) {
            if(!first) {
                map.append(Component.translatable("value_converter.minions.map.display.association.separator"));
            } else {
                first = false;
            }
            map.append(Component.translatable("value_converter.minions.map.display.association", from.getDisplayText(entry.getKey()), to.getDisplayText(entry.getValue())));
        }
        map.append(Component.translatable("value_converter.minions.map.display.default", defaultValue.toString()));
        return Component.translatable("value_converter.minions.map.display", map);
    }

    public static class Type implements ValueConverterType<MapConverter<?,?>> {
        @Override
        public MapCodec<MapConverter<?, ?>> getCodec() {
            return CODEC;
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from, ValueType<?> to) {
            return true;
        }

        @Override
        public <F, T> CompletableFuture<MapConverter<F, T>> configure(MinionsGui parent, ValueType<F> from, ValueType<T> to, @Nullable ValueConverter<?, ?> old) {
            List<Map.Entry<F, T>> entryList;
            T defaultValue = null;
            if(old instanceof MapConverter<?,?> mapConverter && mapConverter.getFrom() == from && mapConverter.getTo() == to) {
                //noinspection unchecked
                entryList = new ArrayList<>(((MapConverter<F, T>)mapConverter).getMap().entrySet());
                //noinspection unchecked
                defaultValue = (T) mapConverter.defaultValue;
            } else {
                entryList = new ArrayList<>();
            }

            CompletableFuture<MapConverter<F, T>> future = new CompletableFuture<>();
            new MapConverterGui<>(parent.viewer, parent, from, to, entryList, defaultValue, future);
            return future;
        }
    }
}
