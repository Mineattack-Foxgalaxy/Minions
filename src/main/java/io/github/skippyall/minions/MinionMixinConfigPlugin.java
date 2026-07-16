package io.github.skippyall.minions;

import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MinionMixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    @Nullable
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.startsWith("io.github.skippyall.minions.mixins.compat.universal_graves.")) {
            return MinionsConfig.get().compat.enableGravesCompat && FabricLoader.getInstance().isModLoaded("universal-graves");
        }
        if(mixinClassName.startsWith("io.github.skippyall.minions.mixins.mobcap.")) {
            return MinionsConfig.get().minion.enableMobCapModification;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    @Nullable
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
