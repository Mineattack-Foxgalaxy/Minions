package io.github.skippyall.minions.util;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TranslationUtil {
    public static <T> String getTranslationKey(T object, Registry<T> registry, String defaultKey) {
        return getTranslationKey(object, registry, registry.getKey().getValue().getPath(), defaultKey);
    }

    public static <T> String getTranslationKey(T object, Registry<T> registry, String prefix, String defaultKey) {
        if(object == null) {
            return defaultKey;
        }

        Identifier id = registry.getId(object);
        if(id == null) {
            return defaultKey;
        }
        return id.toTranslationKey(prefix);
    }

    public static <T> String getTranslationKey(T object, Registry<T> registry) {
        return getTranslationKey(object, registry, "minions.generic.unknown");
    }
}
