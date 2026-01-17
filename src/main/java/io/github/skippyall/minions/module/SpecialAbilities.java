package io.github.skippyall.minions.module;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SpecialAbilities {
    public static final MobSpawningAbility MOB_SPAWNING = Registry.register(MinionRegistries.SPECIAL_ABILITIES, Identifier.of(Minions.MOD_ID, "mob_spawning"), new MobSpawningAbility());
}
