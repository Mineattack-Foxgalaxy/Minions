package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Function;

public interface Clipboard extends TooltipProvider {
    Codec<Clipboard> CODEC = MinionRegistries.CLIPBOARD_TYPES.byNameCodec().dispatch(Clipboard::getCodec, Function.identity());

    MapCodec<? extends Clipboard> getCodec();
}
