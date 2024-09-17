package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class MobSpawningModule {
    public static final SimpleModuleItem MOB_SPAWNING_MODULE = register(Identifier.of(Minions.MOD_ID, "mob_spawning_module"), new SimpleModuleItem(List.of(), List.of(), Items.SPAWNER));

    public static boolean canMinionSpawnMobs(MinionFakePlayer minion) {
        return minion.getModuleInventory().hasModule(MOB_SPAWNING_MODULE);
    }

    public static boolean canMinionDespawnMobs(MinionFakePlayer minion) {
        return minion.getModuleInventory().hasModule(MOB_SPAWNING_MODULE);
    }

    public static void registerMe() {}
}
