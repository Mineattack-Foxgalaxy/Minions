package io.github.skippyall.minions.util;

import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplierType;
import io.github.skippyall.minions.registration.MinionRegistries;
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
        return Component.translatable(getTranslationKey(object, registry));
    }

    public static String getTranslationKey(@Nullable ValueHandlerType<?> handlerType) {
        return switch (handlerType) {
            case null -> "minions.gui.not_set";
            case ValueSupplierType valueSupplierType -> getTranslationKey(
                    valueSupplierType,
                    MinionRegistries.VALUE_SUPPLIER_TYPES,
                    "minions.gui.not_set"
            );
            case ValueConsumerType valueConsumerType -> getTranslationKey(
                    valueConsumerType,
                    MinionRegistries.VALUE_CONSUMER_TYPES,
                    "minions.gui.not_set"
            );
            default -> "minions.generic.unknown";
        };
    }

    public static Component getTranslation(ValueHandlerType<?> handlerType) {
        return Component.translatable(getTranslationKey(handlerType));
    }
}
