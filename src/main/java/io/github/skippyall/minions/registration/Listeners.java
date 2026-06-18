package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.block.instruction_bound.BlockEntityExecutionListener;
import net.minecraft.core.Registry;

public class Listeners {
    public static void register() {
        Registry.register(MinionRegistries.EXECUTING_INSTRUCTION_LISTENER_CODECS, BlockEntityExecutionListener.CODEC_ID, BlockEntityExecutionListener.CODEC);
    }
}
