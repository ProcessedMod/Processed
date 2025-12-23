package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.getFacingDirection
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator

class BigSmelterBlockEntity(pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(ModTileEntities.BIG_SMELTER.get(), pos, blockState) {
    companion object {
        val validator = SquareMultiblockValidator.Builder(3, 3, 3)
            .addMapping('w', Part.block(ModBlocks.BASIC_CASING).or(Part.block(ModBlocks.ITEM_INPUT_HATCH).itemInput()))
            .addMapping('c', Part.controller()).addMapping(' ', Part.air())
            .addRestriction(Part.block(ModBlocks.ITEM_INPUT_HATCH), 1, 1).addLayer(
                "www",
                "w  ",
                "www",
            ).addLayer(
                "www",
                "w w",
                "wcw",
            ).addLayer(
                "www",
                "www",
                "www",
            ).build()
    }

    override fun validator() = validator

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
    override fun getDisplayName(): Component = Translations.bigSmelterName()

    override fun state(): Component = Component.literal("Spitting out Cats")

    override val tier: ProcessedTier = ProcessedTier.Advanced

    override fun tileTickServer(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val input = specialBlock(SpecialBlockType.ItemInput) ?: return
        val be = level.getBlockEntity(input)
        if (be !is InputItemHatchBlockEntity) return
        for (slot in 0..<be.handler.slots) {
            val item = be.handler.extractItem(slot, 1, true)
            if (item.`is`(Items.CAT_SPAWN_EGG)) {
                be.handler.extractItem(slot, 1, false)
                val pos = blockPos.relative(getFacingDirection(state), -1)
                val cat = EntityType.CAT.create(level, {}, pos, MobSpawnType.COMMAND, false, false)
                if (cat != null) level.addFreshEntityWithPassengers(cat)
                return
            }
        }
    }
}
