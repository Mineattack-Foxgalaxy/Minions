package io.github.skippyall.minions.program.value;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class LongValueType implements ValueType<Long> {
    @Override
    public CompletableFuture<Long> openValueDialog(ServerPlayerEntity player, Long previousValue) {
        return null;
    }

    @Override
    public Text getDisplayText(Long value) {
        return null;
    }

    @Override
    public Codec<Long> codec() {
        return null;
    }

    @Override
    public Long defaultValue() {
        return 0L;
    }
}
