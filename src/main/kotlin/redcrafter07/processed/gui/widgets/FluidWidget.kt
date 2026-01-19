package redcrafter07.processed.gui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import redcrafter07.processed.StreamCodecUtil
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.inventory.AbstractProcessedContainerMenu
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.RenderUtils.getFluidColor
import redcrafter07.processed.gui.RenderUtils.getFluidTexture

class FluidWidget(
    x: Int,
    y: Int,
    width: Int,
    val big: Boolean,
    val slot: Int,
    val menu: AbstractProcessedContainerMenu,
) : AbstractWidget(x, y, width, if (big) 60 else 30, Component.empty()) {
    override fun renderWidget(
        guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float
    ) {
        RenderUtils.renderSlot(guiGraphics, x, y, width, height)

        val fluidHandler = menu.getFluid(slot) ?: return
        val fluid = fluidHandler.getFluidInTank(0)
        tooltip = Tooltip.create(Translations.fluidWidgetTooltip(fluid.hoverName, fluid.amount))
        if (!fluid.isEmpty) {
            val sprite = getFluidTexture(fluid, false)
            val color = getFluidColor(fluid)
            val r = FastColor.ARGB32.red(color) / 255f
            val g = FastColor.ARGB32.green(color) / 255f
            val b = FastColor.ARGB32.blue(color) / 255f
            var a = FastColor.ARGB32.alpha(color) / 255f
            if (a == 0f) a = 1f

            val filled = fluidHandler.getFluidInTank(0).amount / fluidHandler.getTankCapacity(0).toDouble()
            val filledHeight = filled * (height - 2)
            guiGraphics.blit(x + 1, y + 1, 0, width - 2, filledHeight.toInt(), sprite, r, g, b, a)
        }

        guiGraphics.blit(RenderUtils.WIDGETS_TEXTURE, x + 1, y + 1, 0, 40, 8, height - 2)
        if (big) guiGraphics.fill(x + 1, y + 29, x + width - 1, y + 30, RenderUtils.DARK_GRAY)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        val fluidHandler = menu.getFluid(slot) ?: return

        val insertionKind =
            if (Screen.hasControlDown()) InsertionKind.InsertOnly else if (Screen.hasAltDown()) InsertionKind.ExtractOnly else InsertionKind.Both
        val carriedItem = this.menu.carried
        if (carriedItem.isEmpty) return
        val itemItem = carriedItem.item
        val cap = carriedItem.getCapability(Capabilities.FluidHandler.ITEM)
        if (cap != null) {
            when (insertionKind) {
                InsertionKind.InsertOnly -> {
                    if (fluidHandler.getFluidInTank(0).amount >= fluidHandler.getTankCapacity(0)) return
                    if (cap.drain(Int.MAX_VALUE, FluidAction.SIMULATE).isEmpty) return
                    if (!fluidHandler.getFluidInTank(0).isEmpty && cap.drain(
                            fluidHandler.getFluidInTank(0).copyWithAmount(Int.MAX_VALUE), FluidAction.SIMULATE
                        ).isEmpty
                    ) return
                }

                InsertionKind.ExtractOnly -> {
                    if (fluidHandler.getFluidInTank(0).isEmpty) return
                    if (cap.fill(
                            fluidHandler.getFluidInTank(0).copy(), FluidAction.SIMULATE
                        ) == 0
                    ) return
                }

                InsertionKind.Both -> Unit
            }

            menu.sendFluidHandlerClickToServer(slot, insertionKind)
        } else if (itemItem is BucketItem) {
            val ty = if (insertionKind == InsertionKind.Both) {
                if (itemItem.content.isSame(Fluids.EMPTY)) InsertionKind.ExtractOnly
                else InsertionKind.InsertOnly
            } else insertionKind

            when (ty) {
                InsertionKind.InsertOnly -> {
                    if (itemItem.content.isSame(Fluids.EMPTY)) return
                    if (fluidHandler.fill(FluidStack(itemItem.content, 1000), FluidAction.SIMULATE) != 1000) return
                }

                InsertionKind.ExtractOnly -> {
                    if (!itemItem.content.isSame(Fluids.EMPTY)) return
                    if (fluidHandler.drain(Int.MAX_VALUE, FluidAction.SIMULATE).isEmpty) return
                }

                else -> throw IllegalStateException()
            }

            menu.sendFluidHandlerClickToServer(slot, insertionKind)
        }
    }

    enum class InsertionKind {
        InsertOnly, ExtractOnly, Both;

        companion object {
            val STREAM_CODEC = StreamCodecUtil.makeEnum(InsertionKind.entries)
        }
    }
}