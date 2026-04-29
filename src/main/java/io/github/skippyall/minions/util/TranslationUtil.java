package io.github.skippyall.minions.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class TranslationUtil {
    public static <T> String getTranslationKey(T object, Registry<T> registry, String defaultKey) {
        return getTranslationKey(object, registry, registry.key().location().getPath(), defaultKey);
    }

    public static <T> String getTranslationKey(T object, Registry<T> registry, String prefix, String defaultKey) {
        if(object == null) {
            return defaultKey;
        }

        ResourceLocation id = registry.getKey(object);
        if(id == null) {
            return defaultKey;
        }
        return id.toLanguageKey(prefix);
    }

    public static <T> String getTranslationKey(T object, Registry<T> registry) {
        return getTranslationKey(object, registry, "minions.generic.unknown");
    }
}
