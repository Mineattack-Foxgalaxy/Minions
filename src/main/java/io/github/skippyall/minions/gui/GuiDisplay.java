package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.skippyall.minions.util.ModelIdUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface GuiDisplay {
    default GuiElementBuilder createElement() {
        return new GuiElementBuilder(createItemStack());
    }

    ItemStack createItemStack();

    record ModelBased(Identifier model, String translationKeyBase, boolean withLore) implements GuiDisplay {
        public ModelBased(Item model, String translationKeyBase, boolean withLore) {
            this(ModelIdUtil.getItemModelId(model), translationKeyBase, withLore);
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(Items.BARRIER);
            stack.set(DataComponentTypes.ITEM_MODEL, model);
            stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(translationKeyBase + ".name"));
            if(withLore) {
                stack.set(DataComponentTypes.LORE, LoreComponent.DEFAULT.with(Text.translatable(translationKeyBase + ".description")));
            }
            return stack;
        }
    }
}
