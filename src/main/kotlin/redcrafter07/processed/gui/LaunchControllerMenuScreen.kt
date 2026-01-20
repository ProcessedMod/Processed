package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal as l
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.Level
import redcrafter07.processed.Translations
import redcrafter07.processed.miner.Planetoid
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

class LaunchControllerMenuScreen(
    menu: LaunchControllerMenu, playerInventory: Inventory, title: Component
) : AbstractContainerScreen<LaunchControllerMenu>(menu, playerInventory, Component.empty()) {
    val level: Level = playerInventory.player.level()

    override fun renderBg(graphics: GuiGraphics, p1: Float, p2: Int, p3: Int) {
        RenderUtils.renderDefault(this, graphics)
        val x = guiLeft + 10
        val y = guiTop + 11
        val textWidth = imageWidth - 22
        RenderUtils.renderCrt(graphics, x - 2, y - 3, imageWidth - 16, font.lineHeight * 6 + 5)
        fun s(c: Component, line: Int = 0) =
            graphics.drawString(font, c, x, y + line * font.lineHeight, RenderUtils.CRT_FG)

        fun s2(c: Component, line: Int = 0) =
            graphics.drawString(font, c, x, y + line * font.lineHeight, RenderUtils.CRT_FG_MUTED)

        val dat = menu.lastLaunched.getOrNull()
        val res = menu.lastResult.getOrNull()
        val destKey = menu.lastDest.getOrNull()
        val dest = menu.level.registryAccess().registry(Planetoid.REGISTRY_KEY).getOrNull()?.get(destKey)
        val now = Instant.now().epochSecond
        if (dat != null && now <= dat.arrivalEpoch) {
            if (now <= dat.planetArrivalEpoch) s(
                l("> ").append(Translations.launchControllerScreenTravelling(dat.planetArrivalEpoch - now))
            ) else s2(l("- ").append(Translations.launchControllerScreenTravelling(0)))

            if (now <= dat.planetArrivalEpoch) s(
                l("  ").append(Translations.launchControllerScreenMining(dat.miningFinishEpoch - now)), 1
            )
            else if (now <= dat.miningFinishEpoch) s(
                l("> ").append(Translations.launchControllerScreenMining(dat.miningFinishEpoch - now)), 1
            )
            else s2(l("  ").append(Translations.launchControllerScreenMining(0)), 1)

            if (now <= dat.miningFinishEpoch) s(
                l("  ").append(Translations.launchControllerScreenTravellingBack(dat.arrivalEpoch - now)), 2
            )
            else s(
                l("> ").append(Translations.launchControllerScreenTravellingBack(dat.arrivalEpoch - now)), 2
            )

            val name = BuiltInRegistries.ITEM.get(dat.item).defaultInstance.hoverName
            s2(Translations.launchControllerScreenResult(name), 3)
            s2(Translations.launchControllerScreenResultAmount(dat.itemAmount), 4)
        } else if (res == null || dest == null) {
            RenderUtils.drawWrapping(
                graphics, font, Translations.launchControllerScreenWaiting(), x, y + font.lineHeight * 2, textWidth
            )

            val storedItems = menu.data.get(3)
            s2(Translations.launchControllerScreenStored())
            if (storedItems < 0) s2(Translations.launchControllerScreenStoredInfinity(), 1)
            else s2(Translations.items(storedItems), 1)
        } else {
            val stored = menu.data.get(0)
            val required = menu.data.get(1)

            s2(Translations.launchControllerScreenFuel(stored, required))
            s2(Translations.launchControllerScreenDestination(Component.translatable(dest.name)), 1)
            s2(Translations.planetoidDistance(Translations.km(dest.distance.get())), 2)
            s2(Translations.launchControllerScreenDuration((res.totalTime * 60.0).toLong()), 3)

            val name = BuiltInRegistries.ITEM.get(dest.resource.get()).defaultInstance.hoverName
            s2(Translations.launchControllerScreenResult(name), 4)
            s2(Translations.launchControllerScreenResultAmount(menu.data.get(2)), 5)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}