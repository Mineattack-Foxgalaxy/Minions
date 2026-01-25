package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerMinionListener;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MinionRegistration {

    public static void register() {
        Registry.register(MinionRegistries.MINION_LISTENER_CODECS, Identifier.of(Minions.MOD_ID, "minion_trigger"), MinionTriggerMinionListener.CODEC);
    }
}
