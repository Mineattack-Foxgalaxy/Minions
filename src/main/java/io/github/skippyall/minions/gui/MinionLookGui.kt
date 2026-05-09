package io.github.skippyall.minions.gui

import com.mojang.authlib.GameProfile
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import io.github.skippyall.minions.gui.input.TextInput
import io.github.skippyall.minions.minion.MinionData
import io.github.skippyall.minions.minion.MinionItem
import io.github.skippyall.minions.minion.MinionProfileUtils
import io.github.skippyall.minions.minion.skin.SkinProvider
import io.github.skippyall.minions.registration.MinionRegistries
import io.github.skippyall.minions.registration.SkinProviders
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import java.util.Optional

class MinionLookGui(
    viewer: ServerPlayer,
    private val minionItem: ItemStack
) : MinionsGui(viewer) {
    private lateinit var gui: SimpleGui

    private var currentSkinProvider: SkinProvider = SkinProviders.NAME

    private val data: MinionData
        get() = MinionItem.getDataOrDefault(viewer.level().server, minionItem)

    init {
        open()
    }

    override fun open() {
        gui = object : SimpleGui(MenuType.GENERIC_9x3, viewer, false) {
            override fun onPlayerClose(success: Boolean) {
                onBackingClosed()
            }
        }
        update()
        gui.open()
    }

    override fun closeBacking() {
        gui.close()
    }

    fun update() {
        updateName()
        updateSkin()
        updateSkinProvider()
    }

    private fun updateName() {
        gui.setSlot(
            10, GuiElementBuilder()
                .setItem(Items.OAK_SIGN)
                .setName(Component.literal(this.data.name))
                .setCallback(this::openRenameGui)
        )
    }

    private fun updateSkin() {
        val builder = GuiElementBuilder()
            .setItem(Items.PLAYER_HEAD)
            .setCallback(this::openSkinGui)

        if (MinionItem.getData(viewer.level().server, minionItem)?.skin?.isPresent ?: false) {
            builder.setComponent(
                DataComponents.PROFILE,
                ResolvableProfile.createResolved(GameProfile(FakePlayer.DEFAULT_UUID, "", this.data.skin.get()))
            )
        }

        gui.setSlot(16, builder)
    }

    private fun updateSkinProvider() {
        gui.setSlot(
            25, GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setComponent(DataComponents.CUSTOM_NAME, currentSkinProvider.getDisplayName())
                .setCallback(Runnable { this.cycleSkinProvider() })
        )
    }

    fun openSkinGui() {
        scope.launch {
            val profile = currentSkinProvider.openSkinMenu(this@MinionLookGui).await()
            val skin = profile.resolveProfile(viewer.level().server.services().profileResolver()).await()

            data.skin = Optional.ofNullable(skin?.properties())

            updateSkin()
        }
    }

    private fun cycleSkinProvider() {
        var currentId = MinionRegistries.SKIN_PROVIDERS.getId(currentSkinProvider)
        currentId++
        if (MinionRegistries.SKIN_PROVIDERS.size() == currentId) {
            currentId = 0
        }

        currentSkinProvider = MinionRegistries.SKIN_PROVIDERS.byId(currentId)!!
        updateSkinProvider()
    }

    fun openRenameGui() {
        scope.launch {
            val newName = TextInput.input(
                this@MinionLookGui,
                Component.translatable("minions.gui.look.rename.title"),
                "Minion",
            ) { name ->
                MinionProfileUtils.checkMinionNameWithoutPrefix(viewer.level().server, name)
            }.await()

            if(newName != null) {
                data.name = newName
                updateName()
            }
        }
    }
}
