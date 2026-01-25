package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.module.MobSpawningAbility;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.module.SpecialAbility;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SpecialAbilities {
    public static final MobSpawningAbility MOB_SPAWNING = register("mob_spawning", new MobSpawningAbility());

    private static <T extends SpecialAbility> T register(String name, T type) {
        Registry.register(MinionRegistries.SPECIAL_ABILITIES, Identifier.of(Minions.MOD_ID, name), type);
        return type;
    }

    public static void register() {}
}
