package io.github.skippyall.minions.clipboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.UUID;
import java.util.function.Consumer;

public record MinionClipboard(UUID minion, String name) implements Clipboard {
    public static final MapCodec<MinionClipboard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("minionUuid").forGetter(MinionClipboard::minion),
                    Codec.STRING.fieldOf("minionName").forGetter(MinionClipboard::name)
            ).apply(instance, MinionClipboard::new)
    );

    @Override
    public MapCodec<? extends Clipboard> getCodec() {
        return CODEC;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("minions.reference.minion.tooltip", name()));
    }
}
