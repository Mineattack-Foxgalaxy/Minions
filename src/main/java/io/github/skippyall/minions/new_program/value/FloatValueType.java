package io.github.skippyall.minions.new_program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.input.TextInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class FloatValueType implements ValueType<Float> {
    @Override
    public Codec<Float> getCodec() {
        return Codec.FLOAT;
    }

    @Override
    public CompletableFuture<Float> openValueDialog(ServerPlayerEntity player, Float previousValue) {
        return TextInput.inputFloat(player, Text.literal("Please enter a decimal number."), previousValue.toString());
    }
}
