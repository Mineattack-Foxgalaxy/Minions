package io.github.skippyall.minions.gui;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;

public interface GuiDisplay {
    Codec<GuiDisplay> CODEC = MinionRegistries.GUI_DISPLAY_TYPE.byNameCodec().dispatch(GuiDisplay::getCodec, codec -> codec.fieldOf("data"));
    GuiDisplay DEFAULT_DISPLAY = new ItemBased(Items.BARRIER);

    static GuiDisplay getGuiDisplay(ResourceLocation id, RegistryAccess manager) {
        return manager.lookup(MinionRegistries.GUI_DISPLAY).map(registry -> registry.getValue(id)).orElse(DEFAULT_DISPLAY);
    }

    static <T> GuiDisplay getGuiDisplayFor(Registry<T> registry, T element, RegistryAccess manager) {
        ResourceLocation elementId = registry.getKey(element);
        if(elementId == null) {
            return DEFAULT_DISPLAY;
        }
        ResourceLocation displayId = elementId.withPrefix(registry.key().location().getPath() + "/");

        return getGuiDisplay(displayId, manager);
    }

    static <T> ItemStack getDisplayStack(Registry<T> registry, T element, RegistryAccess manager) {
        return getGuiDisplayFor(registry, element, manager).createItemStack();
    }

    static <T> ItemStack getDisplayStackWithName(Registry<T> registry, T element, RegistryAccess manager) {
        ItemStack stack = getDisplayStack(registry, element, manager);
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(TranslationUtil.getTranslationKey(element, registry)).withStyle(style -> style.withItalic(false).withColor(ChatFormatting.WHITE)));
        return stack;
    }

    ItemStack createItemStack();

    Codec<? extends GuiDisplay> getCodec();

    class ModelBased implements GuiDisplay {
        public static final Codec<ModelBased> CODEC = ResourceLocation.CODEC.xmap(ModelBased::new, display -> display.model);

        private final ResourceLocation model;

        public ModelBased(ResourceLocation model) {
            this.model = model;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(Items.BARRIER);
            stack.set(DataComponents.ITEM_MODEL, model);
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class ItemBased implements GuiDisplay {
        public static final Codec<ItemBased> CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(ItemBased::new, display -> display.item);

        private final Item item;

        public ItemBased(Item item) {
            this.item = item;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(item);
            stack.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, ReferenceSortedSets.emptySet()));
            stack.set(DataComponents.RARITY, Rarity.COMMON);
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class HeadBased implements GuiDisplay {
        public static final Codec<HeadBased> CODEC = UUIDUtil.AUTHLIB_CODEC.xmap(HeadBased::new, display -> display.uuid);

        private final UUID uuid;

        public HeadBased(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public ItemStack createItemStack() {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap()));
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class StackBased implements GuiDisplay {
        public static final Codec<StackBased> CODEC = ItemStack.CODEC.xmap(StackBased::new, StackBased::createItemStack);

        private final ItemStack stack;

        public StackBased(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack createItemStack() {
            return stack;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }
}
