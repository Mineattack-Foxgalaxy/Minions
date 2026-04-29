package io.github.skippyall.minions.mixinhelper;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;

public class EntityViewMixinHelper {
    public static final ThreadLocal<Predicate<Entity>> ADDITIONAL_PREDICATE = ThreadLocal.withInitial(() -> entity -> true);
}
