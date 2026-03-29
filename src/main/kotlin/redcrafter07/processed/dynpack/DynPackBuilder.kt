package redcrafter07.processed.dynpack

import com.google.gson.JsonObject
import net.minecraft.core.Direction
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.blockstates.VariantProperties.Rotation
import net.minecraft.data.models.model.DelegatedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock
import redcrafter07.processed.block.machine_abstractions.RotationType
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

object DynPackBuilder {
    fun addMaterialBlocks() {
        val oreRL = rl("block/ore_block")
        val oreModel = DelegatedModel(oreRL).get()
        val cableRL = rl("block/cable")
        val cableModel = DelegatedModel(cableRL).get()
        val transporter = rl("block/transporter")
        val transporterModel = DelegatedModel(transporter).get()
        val pipe = rl("block/pipe")
        val pipeModel = DelegatedModel(transporter).get()
        val poweredFurnaceRL = rl("block/powered_furnace")
        val poweredFurnaceModel = DelegatedModel(poweredFurnaceRL).get()
        val creativePowerSourceRL = ResourceLocation.withDefaultNamespace("block/redstone_block")
        val createPowerSourceModel = DelegatedModel(creativePowerSourceRL).get()
        val fluidRL = rl("block/fluid")
        val energyHatchModelRL = rl("block/energy_hatch")
        val energyHatchModel = DelegatedModel(energyHatchModelRL).get()
        val computationHatchModelRL = rl("block/computation_hatch")
        val computationHatchModel = DelegatedModel(computationHatchModelRL).get()

        for (block in ModBlocks.CABLES) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), cableRL).get())
            DynPackResources.addItemModel(block.id, cableModel)
        }

        for (block in ModBlocks.TRANSPORTERS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), transporter).get())
            DynPackResources.addItemModel(block.id, transporterModel)
        }

        for (block in ModBlocks.PIPES) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), pipe).get())
            DynPackResources.addItemModel(block.id, pipeModel)
        }

        for (block in ModBlocks.STONE_ORE_BLOCKS) {
            DynPackResources.addBlockState(block.id, createSimpleBlock(block.get(), oreRL).get())
            DynPackResources.addItemModel(block.id, oreModel)
        }

        for (block in ModBlocks.BLOCKS_POWERED_FURNACE) {
            DynPackResources.addBlockState(block.id, createProcessedBlock(block.get(), poweredFurnaceRL).get())
            DynPackResources.addItemModel(block.id, poweredFurnaceModel)
        }

        for (block in ModBlocks.CREATIVE_POWER_SOURCE) {
            DynPackResources.addBlockState(block.id, createProcessedBlock(block.get(), creativePowerSourceRL).get())
            DynPackResources.addItemModel(block.id, createPowerSourceModel)
        }

        val itemInputHatchModel = rl("block/basic_item_input_hatch")
        DynPackResources.addItemModel(ModBlocks.ITEM_INPUT_HATCH.id, DelegatedModel(itemInputHatchModel).get())
        DynPackResources.addBlockState(
            ModBlocks.ITEM_INPUT_HATCH.id, createSided(ModBlocks.ITEM_INPUT_HATCH.get(), itemInputHatchModel).get()
        )
        val itemOutputHatchModel = rl("block/basic_item_output_hatch")
        DynPackResources.addItemModel(ModBlocks.ITEM_OUTPUT_HATCH.id, DelegatedModel(itemOutputHatchModel).get())
        DynPackResources.addBlockState(
            ModBlocks.ITEM_OUTPUT_HATCH.id, createSided(ModBlocks.ITEM_OUTPUT_HATCH.get(), itemOutputHatchModel).get()
        )

        val fluidInputHatchModel = rl("block/basic_fluid_input_hatch")
        DynPackResources.addItemModel(ModBlocks.FLUID_INPUT_HATCH.id, DelegatedModel(fluidInputHatchModel).get())
        DynPackResources.addBlockState(
            ModBlocks.FLUID_INPUT_HATCH.id, createSided(ModBlocks.FLUID_INPUT_HATCH.get(), fluidInputHatchModel).get()
        )
        val fluidOutputHatchModel = rl("block/basic_fluid_output_hatch")
        DynPackResources.addItemModel(ModBlocks.FLUID_OUTPUT_HATCH.id, DelegatedModel(fluidOutputHatchModel).get())
        DynPackResources.addBlockState(
            ModBlocks.FLUID_OUTPUT_HATCH.id,
            createSided(ModBlocks.FLUID_OUTPUT_HATCH.get(), fluidOutputHatchModel).get()
        )

        for (block in ModBlocks.ENERGY_HATCHES) {
            DynPackResources.addItemModel(block.id, energyHatchModel)
            DynPackResources.addBlockState(block.id, createSided(block.get(), energyHatchModelRL).get())
        }

        for (block in ModBlocks.COMPUTATION_HATCHES) {
            DynPackResources.addItemModel(block.id, computationHatchModel)
            DynPackResources.addBlockState(block.id, createSided(block.get(), computationHatchModelRL).get())
        }

        for (fluid in ModFluids.REGISTERED_FLUIDS) DynPackResources.addBlockState(
            fluid.block.id, createSimpleBlock(fluid.block.get(), fluidRL).get()
        )
    }

    fun addMaterialItems() {
        val dustModel = DelegatedModel(rl("item/dust_item")).get()
        val impureDustModel = DelegatedModel(rl("item/impure_dust_item")).get()
        val pureDustModel = DelegatedModel(rl("item/pure_dust_item")).get()
        val washedDustModel = DelegatedModel(rl("item/washed_dust_item")).get()
        val smallDustModel = DelegatedModel(rl("item/small_dust_item")).get()
        val ingotModel = DelegatedModel(rl("item/ingot_item")).get()
        val rawModel = DelegatedModel(rl("item/raw_item")).get()
        val nuggetModel = DelegatedModel(rl("item/nugget_item")).get()

        for (item in ModItems.DUST_ITEMS) DynPackResources.addItemModel(item.id, dustModel)
        for (item in ModItems.IMPURE_DUST_ITEMS) DynPackResources.addItemModel(item.id, impureDustModel)
        for (item in ModItems.PURE_DUST_ITEMS) DynPackResources.addItemModel(item.id, pureDustModel)
        for (item in ModItems.WASHED_DUST_ITEMS) DynPackResources.addItemModel(item.id, washedDustModel)
        for (item in ModItems.SMALL_DUST_ITEMS) DynPackResources.addItemModel(item.id, smallDustModel)
        for (item in ModItems.INGOT_ITEMS) DynPackResources.addItemModel(item.id, ingotModel)
        for (item in ModItems.NUGGET_ITEMS) DynPackResources.addItemModel(item.id, nuggetModel)
        for (item in ModItems.RAW_MATERIAL_ITEMS) DynPackResources.addItemModel(item.id, rawModel)
        for (fluid in ModFluids.REGISTERED_FLUIDS) {
            // Make all fluids be an item using `neoforge:fluid_container`, with the parent being `neoforge:item/bucket` and the fluid being this bucket's fluid.
            // This will cause them to render in a bucket! Yay!
            val obj = JsonObject()
            obj.addProperty("parent", "neoforge:item/bucket")
            obj.addProperty("fluid", fluid.type.id.toString())
            obj.addProperty("loader", "neoforge:fluid_container")
            DynPackResources.addItemModel(fluid.bucket.id, obj)
        }
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
        val prop = PropertyDispatch.property(BlockProperties.FACING).select(Direction.UP, Variant.variant())
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
            PropertyDispatch.property(BlockProperties.HORIZONTAL_FACING).select(Direction.NORTH, Variant.variant())
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