package io.github.skippyall.minions.client;

import eu.pb4.polymer.networking.api.client.PolymerClientNetworking;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.util.PolymerUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public class MinionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(MinionBlocks.MINION_TRIGGER_BLOCK, BlockRenderLayer.TRANSLUCENT);

        PolymerClientNetworking.registerCommonHandler(PolymerUtil.VersionSyncPayload.class, (client, handler, payload) -> {});
    }
}
