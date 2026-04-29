package io.github.skippyall.minions.minion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MinionConfig {
    public static final Codec<MinionConfig> CODEC = Codec.<Option<?>, Object>dispatchedMap(
            MinionRegistries.MINION_CONFIG_OPTIONS.byNameCodec(),
            Option::codec
    ).xmap(MinionConfig::new, config -> config.values);

    private final Map<Option<?>, Object> values;

    public MinionConfig() {
        values = new HashMap<>();
    }

    private MinionConfig(Map<Option<?>, Object> values) {
        this.values = new HashMap<>(values);
    }

    public <T> T getOption(Option<T> option) {
        if(values.containsKey(option)) {
            //noinspection unchecked
            return (T) values.get(option);
        } else {
            return option.defaultValue();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MinionConfig that)) return false;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }

    public static Option<Boolean> booleanOption(Identifier key, boolean defaultValue) {
        return new Option<>(key, defaultValue, Codec.BOOL);
    }

    public record Option<T>(Identifier key, T defaultValue, Codec<T> codec) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Option<?> option)) return false;
            return Objects.equals(key, option.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }
    }
}
