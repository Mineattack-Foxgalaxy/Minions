package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerMinionListener;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class MinionListeners {
    public static void register() {
        Registry.register(MinionRegistries.MINION_LISTENER_CODECS, ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "minion_trigger"), MinionTriggerMinionListener.CODEC);
    }
}
