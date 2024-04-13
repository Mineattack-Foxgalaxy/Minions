package io.github.skippyall.minions.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;

public class VersionChecker {
    protected static List<UUID> hasSupportedMod = new ArrayList<>();
    public static boolean supportVersion(String version) {
        return version.equals("v0.1");
    }

    public static boolean useSupportedMod(PlayerEntity p) {
        return hasSupportedMod.contains(p.getUuid());
    }

    public static void resetPlayer(UUID uuid) {
        hasSupportedMod.remove(uuid);
    }
}
