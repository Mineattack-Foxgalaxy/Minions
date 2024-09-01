package io.github.skippyall.minions.mixins;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = GameProfile.class, remap = false)
public interface GameProfileMixin{
    @Mutable
    @Accessor("id")
    void setId(UUID id);
}
