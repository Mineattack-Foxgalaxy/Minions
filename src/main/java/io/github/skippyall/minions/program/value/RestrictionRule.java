package io.github.skippyall.minions.program.value;

import io.github.skippyall.minions.gui.input.Result;
import net.minecraft.text.Text;

public interface RestrictionRule<T> {
    Result<Void, Text> validate(T value);

    default boolean matches(T value) {
        return validate(value).isSuccess();
    }
}
