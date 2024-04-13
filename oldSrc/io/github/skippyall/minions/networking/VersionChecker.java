package io.github.skippyall.minions.networking;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VersionChecker {
    protected static List<UUID> hasSupportedMod = new ArrayList<>();
    public static boolean supportVersion(String version) {
        return version.equals("v0.1");
    }

    public static boolean useSupportedMod(Player p) {
        return hasSupportedMod.contains(p.getUUID());
    }

    public static void resetPlayer(UUID uuid) {
        hasSupportedMod.remove(uuid);
    }
}
