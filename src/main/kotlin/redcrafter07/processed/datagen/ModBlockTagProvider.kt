package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.isntVanilla
import java.util.concurrent.CompletableFuture


internal class ModBlockTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider?>,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output, lookupProvider, ProcessedMod.ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        val oreBlocks = ModBlocks.STONE_ORE_BLOCKS.stream().map { it.get() }.toList().toTypedArray()

        val pickaxeMinable = this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.BLITZ_ORE.get())

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.BLITZ_ORE.get())

        val ores = this.tag(Tags.Blocks.ORES).add(ModBlocks.BLITZ_ORE.get())

        this.tag(Tags.Blocks.ORE_RATES_DENSE).add(ModBlocks.BLITZ_ORE.get()).add(*oreBlocks)

        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(ModBlocks.BLITZ_ORE.get()).add(*oreBlocks)

        Materials.getMaterials<MinableOreMaterial>().forEach {
            if (isntVanilla(it.oreBlockItemHolder)) {
                this.tag(it.oreBlockTag()).add(it.oreBlock())
                ores.add(it.oreBlock())
                pickaxeMinable.add(it.oreBlock())
            }
        }

        this.tag(ProcessedTags.Blocks.ORE_BLITZ).add(ModBlocks.BLITZ_ORE.get())
    }
}