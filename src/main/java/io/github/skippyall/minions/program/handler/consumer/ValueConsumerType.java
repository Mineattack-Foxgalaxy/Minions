package io.github.skippyall.minions.program.handler.consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.value.ValueType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class ValueConsumerType extends ValueHandlerType<ValueConsumer> {
    public abstract Codec<? extends ValueConsumer> getCodec();

    public abstract <T> CompletableFuture<? extends ValueConsumer> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueConsumer previous);

    public static class Singleton extends ValueConsumerType implements ValueHandlerType.Singleton<ValueConsumer> {
        private final ValueConsumer consumer;

        public Singleton(ValueConsumer consumer) {
            this.consumer = consumer;
        }

        public ValueConsumer getConsumer() {
            return consumer;
        }

        @Override
        public ValueConsumer getHandler() {
            return consumer;
        }

        @Override
        public Codec<? extends ValueConsumer> getCodec() {
            return MapCodec.unitCodec(consumer);
        }

        @Override
        public <T> CompletableFuture<? extends ValueConsumer> openConfiguration(MinionsGui gui, ValueType<T> valueType, @Nullable ValueConsumer previous) {
            return CompletableFuture.completedFuture(consumer);
        }
    }
}
