package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.consumer.ValueConsumerType;
import io.github.skippyall.minions.websocket.WebsocketValueConsumer;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueConsumers {
    public static final WebsocketValueConsumer.Type WEBSOCKET = register("websocket", new WebsocketValueConsumer.Type());

    public static <T extends ValueConsumerType<MinionRuntime>> T register(String id, T type) {
        return Registry.register(MinionRegistries.VALUE_CONSUMER_TYPES, Identifier.of(Minions.MOD_ID, id), type);
    }

    public static void register() {}
}
