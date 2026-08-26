package io.github.skippyall.minions.block.input;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public interface BlockValueConsumer {
    BlockApiLookup<BlockValueConsumer, @Nullable Direction> SIDED = BlockApiLookup.get(Minions.id("value_acceptor"), BlockValueConsumer.class, Direction.class);

    void acceptValue(TypedValue<?> value);

    ValueType<?> getType();
}
