package io.github.skippyall.minions.gui;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.LinkedHashSet;
import java.util.UUID;

public interface GuiDisplay {
    Codec<GuiDisplay> CODEC = MinionRegistries.GUI_DISPLAY_TYPE.byNameCodec().dispatch(GuiDisplay::getCodec, codec -> codec.fieldOf("data"));
    GuiDisplay DEFAULT_DISPLAY = new ItemBased(Items.BARRIER);

    TooltipDisplay TOOLTIP_HIDE_ALL_COMPONENTS = createHideAllTooltip();

    private static TooltipDisplay createHideAllTooltip() {
        LinkedHashSet<DataComponentType<?>> set = new LinkedHashSet<>();
        for(DataComponentType<?> type : BuiltInRegistries.DATA_COMPONENT_TYPE) {
            if(type != DataComponents.LORE) {
                set.add(type);
            }
        }
        return new TooltipDisplay(false, set);
    }

    static GuiDisplay getGuiDisplay(Identifier id, RegistryAccess manager) {
        return manager.lookup(MinionRegistries.GUI_DISPLAY).map(registry -> registry.getValue(id)).orElse(DEFAULT_DISPLAY);
    }

    static <T> GuiDisplay getGuiDisplayFor(Registry<T> registry, T element, RegistryAccess manager) {
        Identifier elementId = registry.getKey(element);
        if(elementId == null) {
            return DEFAULT_DISPLAY;
        }
        Identifier displayId = elementId.withPrefix(registry.key().identifier().getPath() + "/");

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

    ItemStackTemplate createItemStackTemplate();

    default ItemStack createItemStack() {
        return createItemStackTemplate().create();
    }

    Codec<? extends GuiDisplay> getCodec();

    class ModelBased implements GuiDisplay {
        public static final Codec<ModelBased> CODEC = Identifier.CODEC.xmap(ModelBased::new, display -> display.model);

        private final Identifier model;

        public ModelBased(Identifier model) {
            this.model = model;
        }

        @Override
        public ItemStackTemplate createItemStackTemplate() {
            return new ItemStackTemplate(Items.BARRIER, DataComponentPatch.builder()
                    .set(DataComponents.ITEM_MODEL, model)
                    .build()
            );
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
        public ItemStackTemplate createItemStackTemplate() {
            return new ItemStackTemplate(item, DataComponentPatch.builder()
                    .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_HIDE_ALL_COMPONENTS)
                    .set(DataComponents.RARITY, Rarity.COMMON)
                    .build());
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
        public ItemStackTemplate createItemStackTemplate() {
            return new ItemStackTemplate(Items.PLAYER_HEAD, DataComponentPatch.builder()
                    .set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(uuid))
                    .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_HIDE_ALL_COMPONENTS)
                    .build()
            );
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }

    class StackBased implements GuiDisplay {
        public static final Codec<StackBased> CODEC = ItemStackTemplate.CODEC.xmap(StackBased::new, StackBased::createItemStackTemplate);

        private final ItemStackTemplate template;

        public StackBased(ItemStackTemplate template) {
            this.template = template;
        }

        @Override
        public ItemStackTemplate createItemStackTemplate() {
            return template;
        }

        @Override
        public Codec<? extends GuiDisplay> getCodec() {
            return CODEC;
        }
    }
}
