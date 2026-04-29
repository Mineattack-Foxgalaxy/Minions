package io.github.skippyall.minions.registration;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.clipboard.BlockPosClipboard;
import io.github.skippyall.minions.clipboard.Clipboard;
import io.github.skippyall.minions.clipboard.InstructionClipboard;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ClipboardTypes {
    private static void register(String id, MapCodec<? extends Clipboard> codec) {
        Registry.register(MinionRegistries.CLIPBOARD_TYPES, ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, id), codec);
    }

    static void register() {
        register("instruction", InstructionClipboard.CODEC);
        register("position", BlockPosClipboard.CODEC);
    }
}
