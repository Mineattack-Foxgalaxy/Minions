package io.github.skippyall.minions.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("canAddPassenger")
    boolean minions$canAddPassenger(Entity other);

    @Invoker("getIndirectPassengersStream")
    Stream<Entity> minions$streamIntoPassengers();
}
