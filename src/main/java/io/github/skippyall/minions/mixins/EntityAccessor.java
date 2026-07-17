//code from https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/EntityMixin.java and https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/fakes/EntityInterface.java
package io.github.skippyall.minions.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("canAddPassenger")
    boolean minions$canAddPassenger(Entity other);

    @Invoker("getIndirectPassengersStream")
    Stream<Entity> minions$getIndirectPassengersStream();
}
