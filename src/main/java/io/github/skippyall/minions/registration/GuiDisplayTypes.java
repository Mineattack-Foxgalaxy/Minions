package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.GuiDisplay;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class GuiDisplayTypes {
    static void register() {
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "item"), GuiDisplay.ItemBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "model"), GuiDisplay.ModelBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "head"), GuiDisplay.HeadBased.CODEC);
        Registry.register(MinionRegistries.GUI_DISPLAY_TYPE, Identifier.fromNamespaceAndPath(Minions.MOD_ID, "stack"), GuiDisplay.StackBased.CODEC);
    }
}
