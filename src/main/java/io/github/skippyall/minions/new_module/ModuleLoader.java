package io.github.skippyall.minions.new_module;

import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModuleLoader implements SimpleSynchronousResourceReloadListener {
    public static final HashMap<MinionModule, Identifier> ID_BY_MODULE = new HashMap<>();
    public static final Map<Identifier, MinionModule> MODULES = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Minions.MOD_ID, "module");
    }

    @Override
    public void reload(ResourceManager manager) {
        MODULES.clear();
        ID_BY_MODULE.clear();
        JsonDataLoader.load(manager, ResourceFinder.json("minion_module"), JsonOps.INSTANCE, MinionModule.CODEC, MODULES);
        MODULES.forEach((id, module) -> ID_BY_MODULE.put(module, id));
    }
}
