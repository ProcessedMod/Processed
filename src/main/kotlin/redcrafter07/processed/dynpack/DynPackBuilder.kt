package redcrafter07.processed.dynpack

import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.DelegatedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

object DynPackBuilder {
    fun addMaterialBlocks() {
        val oreRL = rl("block/ore_block")
        val oreModel = DelegatedModel(oreRL).get()
        val metalBlockRL = rl("block/metal_block")
        val metalBlockModel = DelegatedModel(metalBlockRL).get()

        for (block in ModBlocks.STONE_ORE_BLOCKS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), oreRL).get())
            DynPackResources.addItemModel(block.id, oreModel)
        }
        for (block in ModBlocks.METAL_BLOCKS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), metalBlockRL).get())
            DynPackResources.addItemModel(block.id, metalBlockModel)
        }
    }

    fun addMaterialItems() {
        val dustModel = DelegatedModel(rl("item/dust_item")).get()
        val ingotModel = DelegatedModel(rl("item/ingot_item")).get()
        val rawModel = DelegatedModel(rl("item/raw_item")).get()
        val nuggetModels = listOf(
            DelegatedModel(rl("item/nugget_item0")).get(),
            DelegatedModel(rl("item/nugget_item1")).get(),
            DelegatedModel(rl("item/nugget_item2")).get(),
            DelegatedModel(rl("item/nugget_item3")).get(),
        )

        for (item in ModItems.DUST_ITEMS) DynPackResources.addItemModel(item.id, dustModel)
        for (item in ModItems.INGOT_ITEMS) DynPackResources.addItemModel(item.id, ingotModel)
        for (item in ModItems.NUGGET_ITEMS) DynPackResources.addItemModel(
            item.id, nuggetModels[item.get().material.info.nuggetVariant.index]
        )
        for (item in ModItems.RAW_ITEMS) DynPackResources.addItemModel(item.id, rawModel)
    }

    fun createSimpleBlock(block: Block, modelLocation: ResourceLocation): MultiVariantGenerator {
        return MultiVariantGenerator.multiVariant(
            block, Variant.variant().with(VariantProperties.MODEL, modelLocation)
        )
    }
}