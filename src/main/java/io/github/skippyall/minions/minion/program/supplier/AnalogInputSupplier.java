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
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AnalogInputSupplier implements ValueSupplier<Long, MinionRuntime> {
    public static final Codec<AnalogInputSupplier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    World.CODEC.fieldOf("analogInputWorld").forGetter(s -> s.analogInputWorld),
                    BlockPos.CODEC.fieldOf("analogInputPos").forGetter(s -> s.analogInputPos)
            ).apply(instance, AnalogInputSupplier::new));

    private final RegistryKey<World> analogInputWorld;
    private final BlockPos analogInputPos;

    public AnalogInputSupplier(RegistryKey<World> analogInputWorld, BlockPos analogInputPos) {
        this.analogInputWorld = analogInputWorld;
        this.analogInputPos = analogInputPos;
    }

    @Override
    public Long resolve(MinionRuntime minion) {
        World world = minion.getMinion().getServer().getWorld(analogInputWorld);
        if(world != null && world.isPosLoaded(analogInputPos) && world.getBlockState(analogInputPos).isOf(MinionBlocks.ANALOG_INPUT_BLOCK)) {
            return (long) world.getReceivedRedstonePower(analogInputPos);
        } else {
            return 0L;
        }
    }

    @Override
    public ValueType<Long> getValueType() {
        return ValueTypes.LONG;
    }

    @Override
    public ValueSupplierType<MinionRuntime> getType() {
        return ValueSuppliers.ANALOG_INPUT;
    }

    @Override
    public Text getDisplayText() {
        return Text.translatable("value_supplier.minions.analog_input.display", analogInputPos.toShortString(), analogInputWorld.getValue().toString());
    }

    public static class AnalogInputSupplierType extends ValueSupplierType<MinionRuntime> {
        @Override
        public <T> Codec<? extends ValueSupplier<T, MinionRuntime>> getCodec(ValueType<T> type) {
            if(type == ValueTypes.LONG) {
                return ValueSupplier.castCodec(CODEC, ValueTypes.LONG, type);
            }
            return null;
        }

        @Override
        public <T> CompletableFuture<ValueSupplier<?, MinionRuntime>> openConfiguration(MinionsGui parent, ValueType<T> valueType, @Nullable ValueSupplier<?, MinionRuntime> previous) {
            CompletableFuture<ValueSupplier<?, MinionRuntime>> future = new CompletableFuture<>();
            new SimpleMinionsGui(parent, (onClose, me) -> {
                SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, parent.getViewer(), false) {
                    @Override
                    public void onClose() {
                        onClose.run();
                    }
                };
                gui.setTitle(Text.translatable("value_supplier.minions.analog_input"));

                gui.setSlot(4, new GuiElementBuilder(MinionItems.REFERENCE_ITEM)
                        .setCallback(() -> {
                            ItemStack cursor = parent.getViewer().currentScreenHandler.getCursorStack();
                            if (cursor.isOf(MinionItems.REFERENCE_ITEM) && cursor.get(MinionComponentTypes.REFERENCE) instanceof BlockPosClipboard pos) {
                                future.complete(new AnalogInputSupplier(pos.world(), pos.pos()));
                            }
                        })
                        .setItemName(Text.translatable("value_supplier.minions.analog_input.config.click_with_reference"))
                );
                gui.open();
                return gui;
            });
            return future;
        }
    }
}
