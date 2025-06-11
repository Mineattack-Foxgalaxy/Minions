package io.github.skippyall.minions.new_program.value;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.input.TextInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class IntegerValueType implements ValueType<Integer> {
    @Override
    public Codec<Integer> getCodec() {
        return Codec.INT;
    }

    @Override
    public CompletableFuture<Integer> openValueDialog(ServerPlayerEntity player, Integer previousValue) {
        return TextInput.inputInt(player, Text.literal("Please enter an integer number."), previousValue.toString());
    }
}
