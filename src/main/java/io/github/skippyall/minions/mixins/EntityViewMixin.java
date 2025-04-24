package io.github.skippyall.minions.mixins;

import io.github.skippyall.minions.mixinhelpers.EntityViewMixinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(EntityView.class)
public interface EntityViewMixin {
    @ModifyArg(method = "getClosestPlayer(DDDDZ)Lnet/minecraft/entity/player/PlayerEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/EntityView;getClosestPlayer(DDDDLjava/util/function/Predicate;)Lnet/minecraft/entity/player/PlayerEntity;"))
    private @Nullable Predicate<Entity> addMinionPredicate(@Nullable Predicate<Entity> targetPredicate) {
        Predicate<Entity> predicate = EntityViewMixinHelper.ADDITIONAL_PREDICATE.get();
        if(targetPredicate != null) {
            return predicate.and(targetPredicate);
        } else {
            return predicate;
        }
    }
}
