package io.github.skippyall.minions.program.handler;

import net.minecraft.network.chat.Component;

public interface ValueHandler<H extends ValueHandler<H>> {
    ValueHandlerType<H> getType();

    Component getDisplayText();
}
