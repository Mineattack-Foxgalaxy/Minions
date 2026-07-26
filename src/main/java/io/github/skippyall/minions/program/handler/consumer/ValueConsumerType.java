package io.github.skippyall.minions.program.handler.consumer;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueConsumerType extends ValueHandlerType<ValueConsumer> {
    public abstract Codec<? extends ValueConsumer> getCodec();

    public abstract <T> CompletableFuture<? extends ValueConsumer> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueConsumer previous);

    @Override
    public String getTranslationKey() {
        return TranslationUtil.getTranslationKey(
                this,
                MinionRegistries.VALUE_CONSUMER_TYPES,
                "minions.gui.not_set"
        );
    }

    @Override
    public ItemStack getDisplayStack(RegistryAccess access) {
        return GuiDisplay.getDisplayStack(MinionRegistries.VALUE_CONSUMER_TYPES, this, access);
    }
}
