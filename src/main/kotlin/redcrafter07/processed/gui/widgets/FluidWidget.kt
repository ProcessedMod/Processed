package redcrafter07.processed.gui.widgets

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.FastColor
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import redcrafter07.processed.block.tile_entities.capabilities.FluidHandlerModifiable
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.RenderUtils.getFluidColor
import redcrafter07.processed.gui.RenderUtils.getFluidTexture
import redcrafter07.processed.network.FluidHandlerClickPacket
import java.util.function.IntFunction
import java.util.function.Supplier

class FluidWidget(
    x: Int,
    y: Int,
    width: Int,
    val big: Boolean,
    val fluidHandler: FluidHandlerModifiable,
    val pos: BlockPos,
    val tank: Int = 0,
    val carriedItem: Supplier<ItemStack>
) : AbstractWidget(x, y, width, if (big) 60 else 30, Component.empty()) {
    override fun renderWidget(
        guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float
    ) {
        RenderUtils.renderSlot(guiGraphics, x, y, width, height)

        val fluid = fluidHandler.getFluidInTank(tank)
        if (!fluid.isEmpty) {
            val sprite = getFluidTexture(fluid, false)
            val color = getFluidColor(fluid)
            val r = FastColor.ARGB32.red(color) / 255f
            val g = FastColor.ARGB32.green(color) / 255f
            val b = FastColor.ARGB32.blue(color) / 255f
            var a = FastColor.ARGB32.alpha(color) / 255f
            if (a == 0f) a = 1f

            val filled = fluidHandler.getFluidInTank(tank).amount / fluidHandler.getTankCapacity(tank).toDouble()
            val filledHeight = filled * (height - 2)
            guiGraphics.blit(x + 1, y + 1, 0, width - 2, filledHeight.toInt(), sprite, r, g, b, a)
        }

        guiGraphics.blit(RenderUtils.WIDGETS_TEXTURE, x + 1, y + 1, 0, 40, 8, height - 2)
        if (big) guiGraphics.fill(x + 1, y + 29, x + width - 1, y + 30, RenderUtils.DARK_GRAY)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        val ty =
            if (Screen.hasControlDown()) InsertionKind.InsertOnly else if (Screen.hasAltDown()) InsertionKind.ExtractOnly else InsertionKind.Both
        val carriedItem = this.carriedItem.get()
        if (carriedItem.isEmpty) return
        val itemItem = carriedItem.item
        val cap = carriedItem.getCapability(Capabilities.FluidHandler.ITEM)
        if (cap != null) {
            when (ty) {
                InsertionKind.InsertOnly -> {
                    if (fluidHandler.getFluidInTank(tank).amount >= fluidHandler.getTankCapacity(tank)) return
                    if (cap.drain(Int.MAX_VALUE, FluidAction.SIMULATE).isEmpty) return
                    if (!fluidHandler.getFluidInTank(tank).isEmpty && cap.drain(
                            fluidHandler.getFluidInTank(tank).copyWithAmount(Int.MAX_VALUE), FluidAction.SIMULATE
                        ).isEmpty
                    ) return
                }

                InsertionKind.ExtractOnly -> {
                    if (fluidHandler.getFluidInTank(tank).isEmpty) return
                    if (cap.fill(
                            fluidHandler.getFluidInTank(tank).copy(), FluidAction.SIMULATE
                        ) == 0
                    ) return
                }

                InsertionKind.Both -> Unit
            }

            Minecraft.getInstance().connection?.send(FluidHandlerClickPacket(pos, ty))
        } else if (itemItem is BucketItem) {
            val ty = if (ty == InsertionKind.Both) {
                if (itemItem.content.isSame(Fluids.EMPTY)) InsertionKind.ExtractOnly
                else InsertionKind.InsertOnly
            } else ty

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

            Minecraft.getInstance().connection?.send(FluidHandlerClickPacket(pos, ty))
        }
    }

    enum class InsertionKind {
        InsertOnly, ExtractOnly, Both;

        companion object {
            val BY_ID: IntFunction<InsertionKind> = ByIdMap.continuous(
                InsertionKind::ordinal, InsertionKind.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP
            )
            val STREAM_CODEC: StreamCodec<ByteBuf, InsertionKind> =
                ByteBufCodecs.idMapper(BY_ID, InsertionKind::ordinal)
        }
    }
}