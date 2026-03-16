package io.github.skippyall.minions.registration;

import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.clipboard.BlockPosClipboard;
import io.github.skippyall.minions.clipboard.InstructionClipboard;
import io.github.skippyall.minions.clipboard.Clipboard;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ClipboardTypes {
    private static void register(String id, MapCodec<? extends Clipboard> codec) {
        Registry.register(MinionRegistries.CLIPBOARD_TYPES, Identifier.of(Minions.MOD_ID, id), codec);
    }

    static void register() {
        register("instruction", InstructionClipboard.CODEC);
        register("position", BlockPosClipboard.CODEC);
    }
}
