package io.github.skippyall.minions.minion.program.supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.clipboard.BlockPosClipboard;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionBlocks;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionItems;
import io.github.skippyall.minions.registration.ValueSuppliers;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AnalogInputSupplier implements ValueSupplier<Long> {
    public static final Codec<AnalogInputSupplier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("analogInputWorld").forGetter(s -> s.analogInputWorld),
                    BlockPos.CODEC.fieldOf("analogInputPos").forGetter(s -> s.analogInputPos)
            ).apply(instance, AnalogInputSupplier::new));

    private final ResourceKey<Level> analogInputWorld;
    private final BlockPos analogInputPos;

    public AnalogInputSupplier(ResourceKey<Level> analogInputWorld, BlockPos analogInputPos) {
        this.analogInputWorld = analogInputWorld;
        this.analogInputPos = analogInputPos;
    }

    @Override
    public Long resolve(MinecraftServer server) {
        Level world = server.getLevel(analogInputWorld);
        if(world != null && world.isLoaded(analogInputPos) && world.getBlockState(analogInputPos).is(MinionBlocks.ANALOG_INPUT_BLOCK)) {
            return (long) world.getBestNeighborSignal(analogInputPos);
        } else {
            return 0L;
        }
    }

    @Override
    public ValueType<Long> getValueType() {
        return ValueTypes.LONG;
    }

    @Override
    public ValueSupplierType getType() {
        return ValueSuppliers.ANALOG_INPUT;
    }

    @Override
    public Component getDisplayText() {
        return Component.translatable("value_supplier.minions.analog_input.display", analogInputPos.toShortString(), analogInputWorld.identifier().toString());
    }

    public static class AnalogInputSupplierType extends ValueSupplierType {
        @Override
        public Codec<? extends ValueSupplier<?>> getCodec() {
            return CODEC;
        }

        @Override
        public <T> CompletableFuture<ValueSupplier<?>> openConfiguration(MinionsGui parent, ValueType<T> valueType, @Nullable ValueSupplier<?> previous) {
            CompletableFuture<ValueSupplier<?>> future = new CompletableFuture<>();
            new SimpleMinionsGui(parent, (onClose, me) -> {
                SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, parent.viewer, false) {
                    @Override
                    public void onPlayerClose(boolean success) {
                        onClose.run();
                    }
                };
                gui.setTitle(Component.translatable("value_supplier.minions.analog_input"));

                gui.setSlot(2, me.backButton());
                gui.setSlot(4, new GuiElementBuilder(MinionItems.REFERENCE_ITEM)
                        .setCallback(() -> {
                            ItemStack cursor = parent.viewer.containerMenu.getCarried();
                            if (cursor.is(MinionItems.REFERENCE_ITEM) && cursor.get(MinionComponentTypes.REFERENCE) instanceof BlockPosClipboard pos) {
                                future.complete(new AnalogInputSupplier(pos.world(), pos.pos()));
                                me.goBack();
                            }
                        })
                        .setItemName(Component.translatable("value_supplier.minions.analog_input.config.click_with_reference"))
                );
                gui.open();
                return gui;
            });
            return future;
        }
    }
}
