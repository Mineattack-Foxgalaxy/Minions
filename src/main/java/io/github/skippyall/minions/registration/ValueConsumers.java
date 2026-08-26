package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.ConnectedBlockConsumer;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumer;
import io.github.skippyall.minions.program.handler.consumer.ValueConsumerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ValueConsumers {
    public static final ValueConsumerType.Singleton CONNECTED_BLOCK_CONSUMER_TYPE = register("connected_block", new ValueConsumerType.Singleton(ConnectedBlockConsumer.INSTANCE));

    public static <T extends ValueHandlerType<ValueConsumer>> T register(String id, T type) {
        return Registry.register(MinionRegistries.VALUE_CONSUMER_TYPES, Identifier.fromNamespaceAndPath(Minions.MOD_ID, id), type);
    }

    public static void register() {}
}
