package redcrafter07.processed.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.cable.CableBlock
import redcrafter07.processed.block.cable.CableData
import redcrafter07.processed.block.itempipe.ItemPipeBlock
import redcrafter07.processed.block.itempipe.ItemPipeData
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialBlock.*
import redcrafter07.processed.materials.MaterialBlockItem
import redcrafter07.processed.materials.MaterialBlockItem.*
import redcrafter07.processed.materials.MaterialInfo
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.multiblock.CasingBlock
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(ProcessedMod.ID)
    val MATERIAL_BLOCK_ITEMS = ArrayList<DeferredItem<MaterialBlockItem>>()
    val MATERIAL_BLOCKS = ArrayList<DeferredBlock<*>>()

    val BLITZ_ORE = registerBlock("blitz_ore") {
        val props = BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).explosionResistance(1200f)
        Block(props)
    }
    val FLUID_TANK = registerBlock("fluid_tank", ::FluidTankBlock)
    val BLOCKS_POWERED_FURNACE = registerTieredBlock("powered_furnace", ProcessedTier.TIERS, ::PoweredFurnaceBlock)
    val CREATIVE_POWER_SOURCE =
        registerTieredBlock("creative_power_source", ProcessedTier.TIERS, ::CreativePowerSourceBlock)
    val BASIC_CASING = registerBlock("basic_casing") { CasingBlock(BlockBehaviour.Properties.of()) }
    val BIG_SMELTER = registerBlock("big_smelter", ::BigSmelterBlock)


    val CABLES = registerMaterialBlockExtra(
        Materials.MATERIALS, CableData::class.java, { m, _ -> "${m.identifier}_cable" }, ::CableBlock, ::CableBlockItem
    )

    val ITEM_PIPES = registerMaterialBlockExtra(
        Materials.MATERIALS,
        ItemPipeData::class.java,
        { m, _ -> "${m.identifier}_item_pipe" },
        ::ItemPipeBlock,
        ::ItemPipeBlockItem
    )

    val METAL_BLOCKS = registerMaterialBlock(
        Materials.MATERIALS, Material::metalBlockPath, ::MetalBlock, ::MetalBlockItem, MaterialInfo.Types.MetalBlock
    )

    val RAW_METAL_BLOCKS = registerMaterialBlock(
        Materials.MATERIALS,
        Material::rawMetalBlockPath,
        ::RawMetalBlock,
        ::RawMetalBlockItem,
        MaterialInfo.Types.OreLike
    )

    val STONE_ORE_BLOCKS = registerMaterialBlock(
        Materials.MATERIALS, Material::oreBlockPath, ::OreBlock, ::OreBlockItem, MaterialInfo.Types.OreLike
    )

    fun <T : Block> registerBlock(id: String, block: Supplier<T>): DeferredBlock<T> {
        val regBlock = BLOCKS.register(id, block)
        ModItems.registerItem(id) { ModBlockItem(regBlock.get(), Item.Properties(), id) }
        return regBlock
    }

    fun <T : Block, Data> registerMaterialBlockExtra(
        materials: List<Material>,
        dataClass: Class<Data>,
        nameSupplier: BiFunction<Material, Data, String>,
        blockConstructor: Function<Material, T>,
        itemConstructor: BiFunction<Block, Material, MaterialBlockItem>,
    ): List<DeferredBlock<T>> {
        val list = ArrayList<DeferredBlock<T>>()

        for (material in materials) {
            val data = material.getExtraData(dataClass) ?: continue
            val name = nameSupplier.apply(material, data)
            val regBlock = BLOCKS.register(name, Supplier { blockConstructor.apply(material) })
            list.add(regBlock)
            MATERIAL_BLOCKS.add(regBlock)
            val item = ModItems.registerItem(name) { itemConstructor.apply(regBlock.get(), material) }
            MATERIAL_BLOCK_ITEMS.add(item)
        }

        return list
    }


    fun <T : Block> registerMaterialBlock(
        materials: List<Material>,
        nameSupplier: Function<Material, String>,
        blockConstructor: Function<Material, T>,
        itemConstructor: BiFunction<Block, Material, MaterialBlockItem>,
        type: MaterialInfo.Types,
    ): List<DeferredBlock<T>> {
        val list = ArrayList<DeferredBlock<T>>()

        for (material in materials) {
            if (!material.info.types.has(type)) continue
            val name = nameSupplier.apply(material)
            val regBlock = BLOCKS.register(name, Supplier { blockConstructor.apply(material) })
            MATERIAL_BLOCKS.add(regBlock)
            list.add(regBlock)
            val item = ModItems.registerItem(name) { itemConstructor.apply(regBlock.get(), material) }
            MATERIAL_BLOCK_ITEMS.add(item)
        }

        return list
    }

    private fun <T : TieredProcessedBlock> registerTieredBlock(
        id: String, tiers: List<ProcessedTier>, block: TieredBlockProvider<T>
    ): Set<DeferredBlock<T>> {
        return tiers.stream().map { tier ->
            val regBlock = BLOCKS.register("${id}_${tier.named}", Supplier { block.provide(tier) })
            ModItems.registerItem("${id}_${tier.named}") { TieredModBlockItem(regBlock.get(), Item.Properties()) }
            regBlock
        }.collect(Collectors.toSet())
    }

    fun interface TieredBlockProvider<T> {
        fun provide(tier: ProcessedTier): T
    }
}