package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator

class LaunchControllerBlockEntity(pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(ModTileEntities.LAUNCH_CONTROLLER.get(), pos, blockState) {
    companion object {
        val copper_grates = Part.block(Blocks.COPPER_GRATE).or(Part.block(Blocks.WAXED_COPPER_GRATE))
            .or(Part.block(Blocks.EXPOSED_COPPER_GRATE)).or(Part.block(Blocks.WEATHERED_COPPER_GRATE))
            .or(Part.block(Blocks.OXIDIZED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_EXPOSED_COPPER_GRATE))
            .or(Part.block(Blocks.WAXED_WEATHERED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_OXIDIZED_COPPER_GRATE))

        val validator = SquareMultiblockValidator.Builder(5, 5, 5).addMapping('c', Part.controller())
            .addMapping('o', Part.block(ModBlocks.BASIC_CASING).or(Part.block(ModBlocks.ITEM_INPUT_HATCH)))
            .addMapping('i', Part.block(ModBlocks.BASIC_CASING)).addMapping('p', Part.block(Blocks.GRAY_CARPET))
            .addMapping('s', Part.tag(BlockTags.TRAPDOORS)).addMapping('t', copper_grates)
            .addMapping(' ', Part.ignored()).addLayer(
                "ooooo",
                "oiiio",
                "oiiio",
                "oiiio",
                "oocoo",
            ).addLayer(
                " sst ",
                "sppps",
                "sppps",
                "sppps",
                " sss ",
            ).addLayer("   t ", "     ", "     ", "     ", "     ")
            .addLayer("   t ", "     ", "     ", "     ", "     ").addLayer("   t ", "     ", "     ", "     ", "     ")
            .build()
    }

    override fun validator() = validator

    override val tier = ProcessedTier.Advanced

    override fun getDisplayName() = Translations.launchControllerName()
    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
}