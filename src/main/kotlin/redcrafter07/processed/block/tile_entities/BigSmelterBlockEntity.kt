package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector2i
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.multiblock.BlockMapMultiblockValidator
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.multiblock.MultiblockPart
import redcrafter07.processed.multiblock.MultiblockValidator

class BigSmelterBlockEntity(pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(ModTileEntities.BIG_SMELTER.get(), pos, blockState) {
    companion object {
        @Suppress("SpellCheckingInspection")
        val BLOCKMAP = listOf(
            listOf(
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing
            ), listOf(
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Empty,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Controller,
                MultiblockPart.Casing
            ), listOf(
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing,
                MultiblockPart.Casing
            )
        )
    }

    override fun validator(): MultiblockValidator = BlockMapMultiblockValidator(
        MultiblockPart.makeBlockMap(ModBlocks.BASIC_CASING.get(), ModBlocks.BIG_SMELTER.get()), BLOCKMAP, Vector2i(3, 3)
    )
    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
    override fun getDisplayName(): Component = Translations.bigSmelterName()

    override val tier: ProcessedTier = ProcessedTier.Advanced
}