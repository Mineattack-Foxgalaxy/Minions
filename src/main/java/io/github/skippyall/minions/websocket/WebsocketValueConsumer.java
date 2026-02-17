package io.github.skippyall.minions.websocket;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumer;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.ValueConsumers;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class WebsocketValueConsumer<T> implements ValueConsumer<T, MinionRuntime> {
    private final ValueType<T> valueType;
    private final String key;
    private final int id;

    public WebsocketValueConsumer(ValueType<T> valueType, String key, int id) {
        this.valueType = valueType;
        this.key = key;
        this.id = id;
    }

    @Override
    public void consume(T value, MinionRuntime runtime) {
        DataResult<JsonElement> encoded = valueType.codec().encodeStart(JsonOps.INSTANCE, value);
        if(encoded.hasResultOrPartial()) {
            MinionWebsocketManager.get(runtime.getMinion().getUuid()).getRequest(id).acceptReturnValue(key, encoded.getPartialOrThrow());
        }
        encoded.ifError(error -> Minions.LOGGER.error("Error while encoding value for websocket: {}", error.message()));
    }

    @Override
    public ValueType<T> getValueType() {
        return valueType;
    }

    @Override
    public ValueConsumerType<MinionRuntime> getType() {
        return ValueConsumers.WEBSOCKET;
    }

    public static class Type implements ValueConsumerType<MinionRuntime> {
        @Override
        public <T> Codec<? extends ValueConsumer<T, MinionRuntime>> getCodec(ValueType<T> type) {
            return null;
        }

        @Override
        public boolean isConfigurable(ServerPlayerEntity player, ValueType<?> valueType, MinionFakePlayer minion) {
            return false;
        }

        @Override
        public <T> CompletableFuture<? extends ValueConsumer<T, MinionRuntime>> openConfiguration(ServerPlayerEntity player, ValueType<T> valueType, @Nullable ValueConsumer<T, MinionRuntime> previous) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }
    }
}
