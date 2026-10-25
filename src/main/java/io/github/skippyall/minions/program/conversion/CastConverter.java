package io.github.skippyall.minions.program.conversion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.registration.ValueConverters;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CastConverter<T> implements ValueConverter {
    private static final MapCodec<CastConverter<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MinionRegistries.VALUE_TYPES.byNameCodec().fieldOf("to").forGetter(converter -> converter.to)
            ).apply(instance, CastConverter::new)
    );

    private final ValueType<T> to;

    public CastConverter(ValueType<T> to) {
        this.to = to;
    }

    @Override
    public Result<TypedValue<T>, Component> convert(TypedValue<?> fromValue) {
        return Casts.castTypedValue(fromValue, to);
    }

    @Override
    public ValueType<?> getOutputType(ValueType<?> inputType) {
        return to;
    }

    @Override
    public ValueConverterType<?> getType() {
        return ValueConverters.CAST_CONVERTER;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_converter.minions.cast.display", Component.translatable(TranslationUtil.getTranslationKey(to, MinionRegistries.VALUE_TYPES)));
    }

    public static class Type implements ValueConverterType<CastConverter<?>> {
        @Override
        public MapCodec<CastConverter<?>> getCodec() {
            return CastConverter.CODEC;
        }

        @Override
        public boolean isSupportedConversion(ValueType<?> from) {
            return true;
        }

        @Override
        public <F> CompletableFuture<CastConverter<?>> configure(MinionsGui parent, ValueType<F> from, @Nullable ValueConverter old) {
            return PaginatedList.createListFuture(parent, Component.translatable("minions.gui.instruction.converters.cast.output_type"), MinionRegistries.VALUE_TYPES, type -> new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_TYPES, type, parent.viewer.registryAccess())))
                    .thenApply(CastConverter::new);
        }
    }
}
