package redcrafter07.processed.materials

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.materials.data.CraftingMaterial
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.OreMaterial

object CopperMaterial : MaterialBase, MinableOreMaterial, OreMaterial, DustMaterial, IngotMaterial, CraftingMaterial {
    override val identifier = "copper"
    override val color = RenderUtils.color(0xe7, 0x7c, 0x56)
    override var oreBlockHolder: DeferredBlock<Block>? = VANILLA_HOLDER_BLOCK
    override var oreBlockItemHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var impureDustHolder: DeferredItem<Item>? = null
    override var pureDustHolder: DeferredItem<Item>? = null
    override var washedDustHolder: DeferredItem<Item>? = null
    override var rawHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var dustHolder: DeferredItem<Item>? = null
    override var smallDustHolder: DeferredItem<Item>? = null
    override var ingotHolder: DeferredItem<Item>? = VANILLA_HOLDER_ITEM
    override var nuggetHolder: DeferredItem<Item>? = null
    override var plateHolder: DeferredItem<Item>? = null
    override var screwHolder: DeferredItem<Item>? = null
    override var rodHolder: DeferredItem<Item>? = null
    override var wiringHolder: DeferredItem<Item>? = null

    override fun oreBlock(): Block = Blocks.COPPER_ORE
    override fun oreBlockItem(): Item = Items.COPPER_ORE
    override fun rawMaterial(): Item = Items.RAW_COPPER
    override fun ingot(): Item = Items.COPPER_INGOT

    override fun oreBlockItemTag(): TagKey<Item> = Tags.Items.ORES_COPPER
    override fun oreBlockTag(): TagKey<Block> = Tags.Blocks.ORES_COPPER
    override fun rawMaterialTag(): TagKey<Item> = Tags.Items.RAW_MATERIALS_COPPER
    override fun ingotTag(): TagKey<Item> = Tags.Items.INGOTS_COPPER
}