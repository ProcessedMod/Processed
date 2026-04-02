package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.covers.covers.ModCovers
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.isntVanilla

class ModBlockLootTables(provider: HolderLookup.Provider) :
    BlockLootSubProvider(mutableSetOf<Item?>(), FeatureFlags.REGISTRY.allFlags(), provider) {
    override fun generate() {
        createOreLootTable(ModBlocks.BLITZ_ORE.get(), ModItems.BLITZ_ORB.get(), 1f, 3f)

        dropSelf(ModBlocks.FLUID_TANK)
        dropSelf(ModBlocks.BLOCKS_POWERED_FURNACE)
        dropSelf(ModBlocks.BLOCKS_SIFTER)
        dropSelf(ModBlocks.BLOCKS_CRUSHER)
        dropSelf(ModBlocks.BLOCKS_PURIFIER)
        dropSelf(ModBlocks.BLOCKS_WASHER)
        dropSelf(ModBlocks.CREATIVE_POWER_SOURCE)
        dropSelf(ModBlocks.BASIC_CASING)
        dropSelf(ModBlocks.BIG_SMELTER)
        dropSelf(ModBlocks.MASS_SPECTROMETER)
        dropSelf(ModBlocks.MOLECULAR_ANALYZER)
        dropSelf(ModBlocks.MOLECULAR_EXTRUDER)
        dropSelf(ModBlocks.COMPUTER)

        dropSelf(ModBlocks.CABLES)
        dropSelf(ModBlocks.TRANSPORTERS)
        dropSelf(ModBlocks.PIPES)
        dropSelf(ModBlocks.LAUNCH_CONTROLLER)
        dropSelf(ModBlocks.LANDING_PAD)
        dropSelf(ModBlocks.ENERGY_HATCHES)
        dropSelf(ModBlocks.COMPUTATION_HATCHES)
        dropSelf(ModBlocks.ITEM_INPUT_HATCH)
        dropSelf(ModBlocks.ITEM_OUTPUT_HATCH)
        dropSelf(ModBlocks.FLUID_INPUT_HATCH)
        dropSelf(ModBlocks.FLUID_OUTPUT_HATCH)
        dropSelf(ModBlocks.MAGNET)
        dropSelf(ModBlocks.ION_DETECTOR)
        dropSelf(ModBlocks.REINFORCED_GLASS)
        dropSelf(ModBlocks.HEAT_VENT)
        dropSelf(ModBlocks.MATERIAL_ANALYSER_CORE)

        ModCovers.COVERS.entries.forEach { holder -> dropSelf(holder.get().block) }

        Materials.getMaterials<MinableOreMaterial>().forEach {
            if (isntVanilla(it.oreBlockHolder)) {
                ProcessedMod.LOG.info("Making loot table for ${it.oreBlock().descriptionId}")
                this.createOreLootTable(it.oreBlock(), it.rawMaterial(), 1f, 3f)
            } else ProcessedMod.LOG.info("Skipping vanilla block ${it.oreBlock().descriptionId}")
        }
    }

    private fun dropSelf(block: DeferredBlock<*>) {
        dropSelf(block.get())
    }

    private fun <T : Block> dropSelf(blocks: Iterable<DeferredBlock<T>>) {
        for (block in blocks) dropSelf(block)
    }

    private fun createOreLootTable(block: Block, item: Item, min: Float, max: Float) {
        val enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT)

        this.add(
            block, createSilkTouchDispatchTable(
                block, applyExplosionDecay(
                    block,
                    LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
                )
            )
        )
    }

    override fun getKnownBlocks(): Iterable<Block> {
        return ModBlocks.BLOCKS.entries.stream().map { it.value() }.toList()
    }
}