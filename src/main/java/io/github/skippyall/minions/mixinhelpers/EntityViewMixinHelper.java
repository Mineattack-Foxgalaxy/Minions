package io.github.skippyall.minions.mixinhelpers;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public class EntityViewMixinHelper {
    public static final ThreadLocal<Predicate<Entity>> ADDITIONAL_PREDICATE = ThreadLocal.withInitial(() -> entity -> true);
}
