package redcrafter07.processed.datagen

import redcrafter07.processed.ProcessedTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.materials.MaterialBlock
import java.util.concurrent.CompletableFuture


internal class ModBlockTagGenerator(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider?>,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output, lookupProvider, ProcessedMod.ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        val oreBlocks = ModBlocks.STONE_ORE_BLOCKS.stream().map { it.get() }.toList().toTypedArray()

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(*ModBlocks.STONE_ORE_BLOCKS.stream().map { it.get() }.toList().toTypedArray())
            .add(*ModBlocks.METAL_BLOCKS.stream().map { it.get() }.toList().toTypedArray())
            .add(ModBlocks.BLITZ_ORE.get())

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.BLITZ_ORE.get())

        this.tag(Tags.Blocks.ORES).add(*oreBlocks).add(ModBlocks.BLITZ_ORE.get())

        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(ModBlocks.BLITZ_ORE.get()).add(*oreBlocks)

        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(ModBlocks.BLITZ_ORE.get()).add(*oreBlocks)

        for (block in ModBlocks.METAL_BLOCKS) {
            this.tag(block.get().material.materialTag).add(block.get())
        }

        for (block in ModBlocks.STONE_ORE_BLOCKS) {
            val blockInstance: MaterialBlock = block.get()
            this.tag(ProcessedTags.Blocks.commonTag("ores/${block.id.path}")).add(blockInstance)
            this.tag(blockInstance.material.materialTag).add(blockInstance)
        }

        this.tag(ProcessedTags.Blocks.ORE_BLITZ).add(ModBlocks.BLITZ_ORE.get())
    }
}