package redcrafter07.processed.materials.data

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.materials.getMaybeUnregistered

interface MinableOreMaterial: MaterialBase, OreMaterial {
    var oreBlockHolder: DeferredBlock<Block>?
    var oreBlockItemHolder: DeferredItem<Item>?

    fun oreBlock(): Block = getMaybeUnregistered(oreBlockHolder)
    fun oreBlockItem(): Item = getMaybeUnregistered(oreBlockItemHolder)
    fun oreBlockTag(): TagKey<Block> = ProcessedTags.Blocks.commonTag("ores/$identifier")
    fun oreBlockItemTag(): TagKey<Item> = ProcessedTags.Items.commonTag("ores/$identifier")
}