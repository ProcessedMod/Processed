package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
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
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.MaterialBlock
import redcrafter07.processed.rl

class ModBlockLootTables(provider: HolderLookup.Provider) :
    BlockLootSubProvider(mutableSetOf<Item?>(), FeatureFlags.REGISTRY.allFlags(), provider) {
    override fun generate() {
        createOreLootTable(ModBlocks.BLITZ_ORE.get(), ModItems.BLITZ_ORB.get(), 1f, 3f)

        for (oreBlock in ModBlocks.STONE_ORE_BLOCKS) {
            val instance: MaterialBlock = oreBlock.get()
            createOreLootTable(
                instance, BuiltInRegistries.ITEM.get(rl(instance.material.rawPath)), 1f, 2f
            )
        }

        dropSelf(ModBlocks.FLUID_TANK)
        dropSelf(ModBlocks.BLOCKS_POWERED_FURNACE)
        dropSelf(ModBlocks.CREATIVE_POWER_SOURCE)
        dropSelf(ModBlocks.BASIC_CASING)
        dropSelf(ModBlocks.BIG_SMELTER)

        dropSelf(ModBlocks.ITEM_PIPES)
        dropSelf(ModBlocks.CABLES)
        dropSelf(ModBlocks.RAW_METAL_BLOCKS)
        dropSelf(ModBlocks.METAL_BLOCKS)
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