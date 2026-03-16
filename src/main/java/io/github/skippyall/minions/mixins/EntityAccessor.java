package io.github.skippyall.minions.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("canAddPassenger")
    boolean minions$canAddPassenger(Entity other);

    @Invoker("streamIntoPassengers")
    Stream<Entity> minions$streamIntoPassengers();
}
