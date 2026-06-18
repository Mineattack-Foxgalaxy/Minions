package io.github.skippyall.minions.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

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
}
