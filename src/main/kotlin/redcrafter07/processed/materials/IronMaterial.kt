package redcrafter07.processed.materials

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.OreMaterial

object IronMaterial : MaterialBase, MinableOreMaterial, OreMaterial, DustMaterial, IngotMaterial {
    override val identifier = "iron"
    override val color = -1
    override var oreBlockHolder: DeferredBlock<Block>? = VANILLA_HOLDER_BLOCK
    override var oreBlockItemHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var impureDustHolder: DeferredItem<Item>? = null
    override var pureDustHolder: DeferredItem<Item>? = null
    override var washedDustHolder: DeferredItem<Item>? = null
    override var rawHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var dustHolder: DeferredItem<Item>? = null
    override var smallDustHolder: DeferredItem<Item>? = null
    override var ingotHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var nuggetHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM

    override fun oreBlock(): Block = Blocks.IRON_ORE
    override fun oreBlockItem(): Item = Items.IRON_ORE
    override fun rawMaterial(): Item = Items.RAW_IRON
    override fun ingot(): Item = Items.IRON_INGOT
    override fun nugget(): Item = Items.IRON_NUGGET

    override fun oreBlockItemTag(): TagKey<Item> = Tags.Items.ORES_IRON
    override fun oreBlockTag(): TagKey<Block> = Tags.Blocks.ORES_IRON
    override fun rawMaterialTag(): TagKey<Item> = Tags.Items.RAW_MATERIALS_IRON
    override fun ingotTag(): TagKey<Item> = Tags.Items.INGOTS_IRON
    override fun nuggetTag(): TagKey<Item> = Tags.Items.NUGGETS_IRON
}