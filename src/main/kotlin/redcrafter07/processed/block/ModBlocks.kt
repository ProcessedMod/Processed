package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.TransparentBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.util.TriConsumer
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.MaterialBlock.OreBlock
import redcrafter07.processed.materials.MaterialBlockItem.*
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.isntVanilla
import redcrafter07.processed.transmitters.Transmitters
import redcrafter07.processed.transmitters.cable.CableBlock
import redcrafter07.processed.transmitters.pipes.PipeBlock
import redcrafter07.processed.transmitters.transporters.TransporterBlock
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(ProcessedMod.ID)

    val BLITZ_ORE = registerBlock("blitz_ore") {
        val props = Properties.ofFullCopy(Blocks.DIAMOND_ORE).explosionResistance(1200f)
        Block(props)
    }
    val FLUID_TANK = registerBlock("fluid_tank", ::FluidTankBlock)
    val BLOCKS_POWERED_FURNACE = registerTieredBlock("powered_furnace", ProcessedTier.TIERS, ::PoweredFurnaceBlock)
    val BLOCKS_SIFTER = registerTieredBlock("sifter", ProcessedTier.TIERS, ::SifterBlock)
    val BLOCKS_CRUSHER = registerTieredBlock("crusher", ProcessedTier.TIERS, ::CrusherBlock)
    val BLOCKS_PURIFIER = registerTieredBlock("purifier", ProcessedTier.TIERS, ::PurifierBlock)
    val BLOCKS_WASHER = registerTieredBlock("washer", ProcessedTier.TIERS, ::WasherBlock)
    val CREATIVE_POWER_SOURCE =
        registerTieredBlock("creative_power_source", ProcessedTier.TIERS, ::CreativePowerSourceBlock)
    val BASIC_CASING = registerBlock("basic_casing") { Block(Properties.of()) }
    val BIG_SMELTER = registerBlock("big_smelter", ::BigSmelterBlock)
    val LAUNCH_CONTROLLER = registerBlock("launch_controller", ::LaunchControllerBlock)
    val LANDING_PAD = registerBlock("landing_pad", ::LandingPadBlock)
    val ITEM_INPUT_HATCH = registerBlock("item_input_hatch", ::InputItemHatchBlock)
    val ITEM_OUTPUT_HATCH = registerBlock("item_output_hatch", ::OutputItemHatchBlock)
    val FLUID_INPUT_HATCH = registerBlock("fluid_input_hatch", ::InputFluidHatchBlock)
    val FLUID_OUTPUT_HATCH = registerBlock("fluid_output_hatch", ::OutputFluidHatchBlock)
    val COMPUTER = registerBlock("computer", ::ComputerBlock)

    val MAGNET = registerBlock("magnet") { Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)) }
    val ION_DETECTOR = registerBlock("ion_detector") { Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)) }
    val REINFORCED_GLASS = registerBlock("reinforced_glass") {
        val props =
            Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.GLASS).noOcclusion().isValidSpawn(Blocks::never)
                .isRedstoneConductor(::never).isSuffocating(::never).isViewBlocking(::never)
        TransparentBlock(props)
    }
    val HEAT_VENT = registerBlock("heat_vent", ::HeatVentBlock)
    val MATERIAL_ANALYSER_CORE =
        registerBlock("material_analyser_core") { Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)) }

    val ENERGY_HATCHES = registerTieredBlock("energy_hatch", ProcessedTier.TIERS, ::EnergyHatchBlock)
    val COMPUTATION_HATCHES = registerTieredBlock("computation_hatches", ProcessedTier.tiersFrom(ProcessedTier.Advanced), ::ComputationHatchBlock)

    val CABLES = Transmitters.cables.map { (tier, material) ->
        registerBlockSpecial(
            "${material.identifier}_cable",
            { CableBlock(material, tier) },
            { CableBlockItem(it, material, tier) })
    }
    val TRANSPORTERS = Transmitters.transporters.map { (speed, material) ->
        registerBlockSpecial(
            "${material.identifier}_transporter",
            { TransporterBlock(material, speed) },
            { TransporterBlockItem(it, material, speed) })
    }
    val PIPES = Transmitters.pipes.map { (speed, material) ->
        registerBlockSpecial(
            "${material.identifier}_pipe",
            { PipeBlock(material, speed) },
            { PipeBlockItem(it, material, speed) })
    }

    val STONE_ORE_BLOCKS = registerMaterialBlocks<MinableOreMaterial>(
        { isntVanilla(it.oreBlockHolder) },
        { it.identifier + "_ore" },
        ::OreBlock,
        ::OreBlockItem,
        { v, item, block -> v.oreBlockHolder = block; v.oreBlockItemHolder = item })

    inline fun <reified T : MaterialBase> registerMaterialBlocks(
        filter: Predicate<T>,
        name: Function<T, String>,
        blockConstructor: Function<T, Block>,
        itemConstructor: BiFunction<Block, T, Item>,
        done: TriConsumer<T, DeferredItem<Item>, DeferredBlock<Block>>
    ): List<DeferredBlock<Block>> = Materials.getMaterials<T>().filter(filter).map {
        val name = name.apply(it)
        val block = BLOCKS.register(name, Supplier { blockConstructor.apply(it) })
        val item = ModItems.registerItem(name) { itemConstructor.apply(block.get(), it) }
        done.accept(it, item, block)
        block
    }.toList()

    fun <T : Block> registerBlock(id: String, block: Supplier<T>): DeferredBlock<T> {
        val regBlock = BLOCKS.register(id, block)
        ModItems.registerItem(id) { ModBlockItem(regBlock.get(), Item.Properties(), id) }
        return regBlock
    }

    fun <T : Block> registerBlockSpecial(
        id: String, block: Supplier<T>, item: Function<Block, BlockItem>
    ): DeferredBlock<T> {
        val regBlock = BLOCKS.register(id, block)
        ModItems.registerItem(id) { item.apply(regBlock.get()) }
        return regBlock
    }

    private fun <T> registerTieredBlock(
        id: String, tiers: List<ProcessedTier>, block: TieredBlockProvider<T>
    ): Set<DeferredBlock<T>> where T : Block, T : TieredBlock {
        return tiers.stream().map { tier ->
            val regBlock = BLOCKS.register("${id}_${tier.named}", Supplier { block.provide(tier) })
            ModItems.registerItem("${id}_${tier.named}") { TieredModBlockItem(regBlock.get(), Item.Properties()) }
            regBlock
        }.collect(Collectors.toSet())
    }

    fun interface TieredBlockProvider<T> {
        fun provide(tier: ProcessedTier): T
    }

    fun never(ignored1: BlockState, ignored2: BlockGetter, ignored3: BlockPos) = false
}