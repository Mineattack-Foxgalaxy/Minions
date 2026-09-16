package io.github.skippyall.minions.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CodecUtil {
    public static <T, S extends T> Codec<T> checkedSuperclassCodec(Codec<S> subclassCodec, Class<S> subclass) {
        return new Codec<>() {
            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                return subclassCodec.decode(ops, input).map(p -> p.mapFirst(v -> v));
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                if(subclass.isInstance(input)) {
                    S castInput = subclass.cast(input);
                    return subclassCodec.encode(castInput, ops, prefix);
                } else {
                    return DataResult.error(() -> "Input must be a subclass of " + subclass + ", but was " + input.getClass());
                }
            }
        };
    }

    public static <K, V> Codec<Map<K, V>> createMapAsListCodec(Codec<K> key, Codec<V> value, String keyName, String valueName) {
        return Codec.mapPair(
                key.fieldOf(keyName),
                value.fieldOf(valueName)
        ).codec().listOf().comapFlatMap(
                list -> {
                    Map<K, V> map = new LinkedHashMap<>();
                    List<Pair<K, V>> missingKeys = new ArrayList<>();
                    for(Pair<K, V> pair : list) {
                        V previous = map.putIfAbsent(pair.getFirst(), pair.getSecond());
                        if(previous != null) {
                            missingKeys.add(pair);
                        }
                    }
                    if(missingKeys.isEmpty()) {
                        return DataResult.success(map);
                    } else {
                        return DataResult.error(() -> {
                            StringBuilder builder = new StringBuilder("Pairs could not be added due to duplicate keys: ");
                            for(Pair<K, V> pair : missingKeys) {
                                builder.append(pair);
                            }
                            return builder.toString();
                        });
                    }
                }, map -> {
                    List<Pair<K, V>> list = new ArrayList<>();

                    for(Map.Entry<K, V> entry : map.entrySet()) {
                        list.add(Pair.of(entry.getKey(), entry.getValue()));
                    }
                    return list;
                }
        );
    }
}
