package redcrafter07.processed.covers.covers

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.DynamicContainerScreen

class FluidCoverScreen(menu: FluidCoverMenu, playerInventory: Inventory, title: Component) :
    DynamicContainerScreen<FluidCoverMenu>(menu, playerInventory, title) {
    var pushingButton: Button? = null

    override fun init() {
        super.init()

        pushingButton =
            addRenderableWidget(Button.Builder(buttonMessage()) { menu.rpcSender.sendToServer("togglePushing") }
                .pos(guiLeft + 10, guiTop + 30).build())
    }

    fun onPushChange() {
        val btn = pushingButton ?: return
        btn.message = buttonMessage()
    }

    fun buttonMessage(): MutableComponent =
        if (menu.pushing) Translations.buttonPushPullPush() else Translations.buttonPushPullPull()
}