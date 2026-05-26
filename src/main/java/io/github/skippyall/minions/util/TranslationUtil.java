package io.github.skippyall.minions.util;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class TranslationUtil {
    public static <T> String getTranslationKey(@Nullable T object, Registry<T> registry, String defaultKey) {
        return getTranslationKey(object, registry, registry.key().identifier().getPath(), defaultKey);
    }

    public static <T> String getTranslationKey(@Nullable T object, Registry<T> registry, String prefix, String defaultKey) {
        if(object == null) {
            return defaultKey;
        }

        Identifier id = registry.getKey(object);
        if(id == null) {
            return defaultKey;
        }
        return id.toLanguageKey(prefix);
    }

    public static <T> String getTranslationKey(@Nullable T object, Registry<T> registry) {
        return getTranslationKey(object, registry, "minions.generic.unknown");
    }

    public static <T> Component getTranslation(@Nullable T object, Registry<T> registry) {
        return Component.translatable(TranslationUtil.getTranslationKey(object, registry));
    }
}
