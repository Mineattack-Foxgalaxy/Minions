package io.github.skippyall.minions.program.handler;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueHandlerType<H> {
    public abstract Codec<? extends H> getCodec();

    public abstract <T> CompletableFuture<? extends H> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable H previous);

    public abstract String getTranslationKey();

    public Component getTranslation() {
        return Component.translatable(getTranslationKey());
    }

    public abstract ItemStack getDisplayStack(RegistryAccess access);

    public interface Singleton<H> {
        H getHandler();
    }
}
