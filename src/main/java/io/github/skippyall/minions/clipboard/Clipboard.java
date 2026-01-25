package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.item.tooltip.TooltipAppender;

import java.util.function.Function;

public interface Clipboard extends TooltipAppender {
    Codec<Clipboard> CODEC = MinionRegistries.CLIPBOARD_TYPES.getCodec().dispatch(Clipboard::getCodec, Function.identity());

    MapCodec<? extends Clipboard> getCodec();
}
