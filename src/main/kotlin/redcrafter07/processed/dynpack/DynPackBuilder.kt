package redcrafter07.processed.dynpack

import net.minecraft.core.Direction
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.blockstates.VariantProperties.Rotation
import net.minecraft.data.models.model.DelegatedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock
import redcrafter07.processed.block.machine_abstractions.RotationType
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

object DynPackBuilder {
    fun addMaterialBlocks() {
        val oreRL = rl("block/ore_block")
        val oreModel = DelegatedModel(oreRL).get()
        val metalBlockRL = rl("block/metal_block")
        val metalBlockModel = DelegatedModel(metalBlockRL).get()
        val cableRL = rl("block/cable")
        val cableModel = DelegatedModel(cableRL).get()
        val itemPipeRL = rl("block/item_pipe")
        val itemPipeModel = DelegatedModel(itemPipeRL).get()
        val rawMetalBlockRLS = listOf(rl("block/raw_metal_block0"), rl("block/raw_metal_block1"))
        val rawMetalBlocks = rawMetalBlockRLS.map { DelegatedModel(it).get() }.toList()
        val poweredFurnaceRL = rl("block/powered_furnace")
        val poweredFurnaceModel = DelegatedModel(poweredFurnaceRL).get()
        val creativePowerSourceRL = ResourceLocation.withDefaultNamespace("block/redstone_block")
        val createPowerSourceModel = DelegatedModel(creativePowerSourceRL).get()

        for (block in ModBlocks.CABLES) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), cableRL).get())
            DynPackResources.addItemModel(block.id, cableModel)
        }

        for (block in ModBlocks.ITEM_PIPES) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), itemPipeRL).get())
            DynPackResources.addItemModel(block.id, itemPipeModel)
        }

        for (block in ModBlocks.STONE_ORE_BLOCKS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), oreRL).get())
            DynPackResources.addItemModel(block.id, oreModel)
        }
        for (block in ModBlocks.METAL_BLOCKS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), metalBlockRL).get())
            DynPackResources.addItemModel(block.id, metalBlockModel)
        }

        for (block in ModBlocks.RAW_METAL_BLOCKS) {
            val idx = block.get().material.info.rawBlockVariant.index
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), rawMetalBlockRLS[idx]).get())
            DynPackResources.addItemModel(block.id, rawMetalBlocks[idx])
        }

        for (block in ModBlocks.BLOCKS_POWERED_FURNACE) {
            DynPackResources.addBlockState(block.id, createProcessedBlock(block.get(), poweredFurnaceRL).get())
            DynPackResources.addItemModel(block.id, poweredFurnaceModel)
        }

        for (block in ModBlocks.CREATIVE_POWER_SOURCE) {
            DynPackResources.addBlockState(block.id, createProcessedBlock(block.get(), creativePowerSourceRL).get())
            DynPackResources.addItemModel(block.id, createPowerSourceModel)
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

    fun createProcessedBlock(block: ProcessedBlock, modelLocation: ResourceLocation): MultiVariantGenerator =
        when (block.rotationType()) {
            RotationType.RotatableHorizontal -> createSidedHorizontal(block, modelLocation)
            RotationType.Rotatable -> createSided(block, modelLocation)
            RotationType.NonRotatable -> createSimpleBlock(block, modelLocation)
        }

    fun createSided(block: Block, modelLocation: ResourceLocation): MultiVariantGenerator {
        fun xy(x: Rotation, y: Rotation): Variant =
            Variant.variant().with(VariantProperties.X_ROT, x).with(VariantProperties.Y_ROT, y)

        // create a PropertyDispatch for the facing=... property, with different x and y rotations according to the direction.
        val prop = PropertyDispatch.property(ProcessedBlock.STATE_FACING).select(Direction.UP, Variant.variant())
            .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, Rotation.R180))
            .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, Rotation.R90))
            .select(Direction.SOUTH, xy(Rotation.R90, Rotation.R180))
            .select(Direction.WEST, xy(Rotation.R90, Rotation.R270))
            .select(Direction.EAST, xy(Rotation.R90, Rotation.R90))

        // add the propertydispatch to the blockstate
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation))
            .with(prop)
    }

    fun createSidedHorizontal(block: Block, modelLocation: ResourceLocation): MultiVariantGenerator {
        // create a PropertyDispatch for the facing=... property, with different y rotations according to the direction.
        val prop =
            PropertyDispatch.property(ProcessedBlock.STATE_HORIZ_FACING).select(Direction.NORTH, Variant.variant())
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, Rotation.R180))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, Rotation.R270))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, Rotation.R90))

        // add the propertydispatch to the blockstate
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation))
            .with(prop)
    }

    fun createSimpleBlock(block: Block, modelLocation: ResourceLocation): MultiVariantGenerator {
        return MultiVariantGenerator.multiVariant(
            block, Variant.variant().with(VariantProperties.MODEL, modelLocation)
        )
    }
}