package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.mixinhelper.EntityViewMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(EntityGetter.class)
public interface EntityGetterMixin {
    @ModifyArg(method = "getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/EntityGetter;getNearestPlayer(DDDDLjava/util/function/Predicate;)Lnet/minecraft/world/entity/player/Player;"))
    private @Nullable Predicate<Entity> addMinionPredicate(@Nullable Predicate<Entity> targetPredicate) {
        Predicate<Entity> predicate = EntityViewMixinHelper.ADDITIONAL_PREDICATE.get();
        if(targetPredicate != null) {
            return predicate.and(targetPredicate);
        } else {
            return predicate;
        }
    }
}
