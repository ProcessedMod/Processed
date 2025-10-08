package redcrafter07.processed.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.gui.widgets.IoToggleButton
import redcrafter07.processed.network.IOChangePacket
import java.util.*

class ConfigScreen(val machine: ProcessedMachine, val pos: BlockPos) : Screen(machine.displayName) {
    var topX = 0
    var topY = 0
    var isItem = machine.supportedItemHandlers.size > 1

    override fun init() {
        super.init()
        val facing = getFacingBlockStates(pos)

        topX = (width - RenderUtils.GUI_BASE_TEXTURE_WIDTH) / 2
        topY = (height - RenderUtils.GUI_BASE_TEXTURE_HEIGHT) / 2

        for (side in BlockSide.entries) {
            val pos = side.buttonPos
            val supportedStates = if (isItem) machine.supportedItemHandlers
            else machine.supportedFluidHandlers

            addRenderableWidget(
                IoToggleButton(
                    topX + pos.x,
                    topY + pos.y,
                    side.translatedName,
                    machine.getSide(isItem, side),
                    supportedStates,
                    facing.get(side)
                ) { _, newState ->
                    machine.setSide(isItem, side, newState)
                    machine.invalidateCapabilities()
                    Minecraft.getInstance().connection?.send(IOChangePacket(this.pos, newState, side, isItem))
                })
        }

        if (machine.supportedItemHandlers.size > 1 && machine.supportedFluidHandlers.size > 1) {
            val itemButton = addRenderableWidget(
                Button.builder(Component.literal("I")) { ioSwitchToItems() }.pos(topX + 110, topY + 32).size(14, 14)
                    .build()
            )
            itemButton.active = !isItem


            val fluidButton = addRenderableWidget(
                Button.builder(Component.literal("F")) { ioSwitchToFluids() }.pos(topX + 110, topY + 50).size(14, 14)
                    .build()
            )
            fluidButton.active = isItem
        }
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, RenderUtils.GUI_BASE_TEXTURE)
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680)
        graphics.blit(
            RenderUtils.GUI_BASE_TEXTURE,
            topX,
            topY,
            0,
            0,
            RenderUtils.GUI_BASE_TEXTURE_WIDTH,
            RenderUtils.GUI_BASE_TEXTURE_HEIGHT
        )
        graphics.drawString(font, title, topX + 8, topY + 6, 4210752, false)
        graphics.drawString(
            font,
            if (isItem) Translations.configuringItemsLabel() else Translations.configuringFluidsLabel(),
            topX + 8,
            topY + 18,
            4210752,
            false
        )
    }

    override fun isPauseScreen(): Boolean = false

    private fun ioSwitchToItems() {
        isItem = true
        rebuildWidgets()
    }

    private fun ioSwitchToFluids() {
        isItem = false
        rebuildWidgets()
    }

    private fun getFacingBlockStates(pos: BlockPos): EnumMap<BlockSide, ItemStack> {
        val player = Minecraft.getInstance().player ?: return EnumMap(BlockSide::class.java)
        val level = player.level()
        val map = EnumMap<BlockSide, ItemStack>(BlockSide::class.java)
        val machine = level.getBlockState(pos)

        for (dir in Direction.entries) {
            val otherBlockPos = pos.relative(dir)
            val otherBlockState = level.getBlockState(otherBlockPos)
            if (otherBlockState.isEmpty || otherBlockState.isAir) continue
            val item = otherBlockState.getCloneItemStack(
                BlockHitResult(
                    otherBlockPos.center.relative(dir.opposite, 0.5), dir.opposite, otherBlockPos, false
                ), level, otherBlockPos, player
            )
            if (item.isEmpty) continue
            map[BlockSide.translateDirection(dir, machine)] = item
        }

        return map
    }

}