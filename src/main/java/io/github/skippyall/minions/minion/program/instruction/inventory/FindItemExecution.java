package io.github.skippyall.minions.minion.program.instruction.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;

public class FindItemExecution implements InstructionExecution {
    public static final Codec<FindItemExecution> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("itemId").forGetter(e -> e.itemId),
                    Codec.BOOL.fieldOf("screen").forGetter(e -> e.screen)
            ).apply(instance, FindItemExecution::new)
    );

    public static final Parameter<String> ITEM_ID = new Parameter<>("item_id", ValueTypes.STRING);
    public static final Parameter<Boolean> SCREEN = new Parameter<>("screen", ValueTypes.BOOLEAN);

    public static final Parameter<Long> SLOT = new Parameter<>("slot", ValueTypes.LONG);

    private String itemId;
    private boolean screen;

    public FindItemExecution(ParameterValueList params, Context context) {
        this.itemId = params.getValue(ITEM_ID);
        this.screen = params.getValue(SCREEN);
    }

    public FindItemExecution(String itemId, boolean screen) {
        this.itemId = itemId;
        this.screen = screen;
    }

    @Override
    public boolean isDone(Context context) {
        return true;
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        AbstractContainerMenu menu = SwapItemExecution.getScreen(context.getOrThrow(ExecutionContext.MINION_KEY), screen);
        Identifier identifier = Identifier.tryParse(itemId);
        Item item = BuiltInRegistries.ITEM.getValue(identifier);
        //noinspection ConstantValue (wrong nullability)
        if(item != null && menu != null) {
            for(Slot slot : menu.slots) {
                if(slot.getItem().getItem() == item) {
                    list.setValue(SLOT, (long) slot.index);
                    return;
                }
            }
        }
    }
}
