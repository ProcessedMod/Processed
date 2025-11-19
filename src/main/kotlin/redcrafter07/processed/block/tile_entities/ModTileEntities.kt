package redcrafter07.processed.block.tile_entities

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.cable.CableBlockEntity
import java.util.function.Supplier

object ModTileEntities {
    val BLOCK_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessedMod.ID)

    val POWERED_FURNACE =
        register("powered_furnace", ::PoweredFurnaceBlockEntity, *ModBlocks.BLOCKS_POWERED_FURNACE.toTypedArray())
    val FLUID_TANK = register("fluid_tank", ::FluidTankBlockEntity, ModBlocks.FLUID_TANK)
    val BIG_SMELTER = register("big_smelter", ::BigSmelterBlockEntity, ModBlocks.BIG_SMELTER)
    val LAUNCH_CONTROLLER = register("launch_controller", ::LaunchControllerBlockEntity, ModBlocks.LAUNCH_CONTROLLER)
    val CABLE = register("cable", ::CableBlockEntity, *ModBlocks.CABLES.toTypedArray())
    val ITEM_PIPE = register("item_pipe", ::ItemPipeBlockEntity, *ModBlocks.ITEM_PIPES.toTypedArray())
    val CREATIVE_POWER_SOURCE = register(
        "creative_power_source", ::CreativePowerSourceBlockEntity, *ModBlocks.CREATIVE_POWER_SOURCE.toTypedArray()
    )
    val INPUT_ITEM_HATCH = register("input_item_hatch", ::InputItemHatchBlockEntity, ModBlocks.ITEM_INPUT_HATCH)
    val OUTPUT_ITEM_HATCH = register("output_item_hatch", ::OutputItemHatchBlockEntity, ModBlocks.ITEM_OUTPUT_HATCH)
    val INPUT_FLUID_HATCH = register("input_fluid_hatch", ::InputFluidHatchBlockEntity, ModBlocks.FLUID_INPUT_HATCH)
    val OUTPUT_FLUID_HATCH = register("output_fluid_hatch", ::InputFluidHatchBlockEntity, ModBlocks.FLUID_OUTPUT_HATCH)
    val ENERGY_HATCH = register("energy_hatch", ::EnergyHatchBlockEntity, ModBlocks.ENERGY_HATCH)

    private fun <T : BlockEntity> register(
        name: String, blockEntity: BlockEntitySupplier<T>, vararg blocks: DeferredBlock<*>
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        return BLOCK_TYPES.register(
            name, Supplier {
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") BlockEntityType(
                    blockEntity, blocks.map { it.get() }.toSet(), null
                )
            })
    }
}