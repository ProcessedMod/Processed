package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.rl

class ModBlockStateProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, ProcessedMod.ID, existingFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BLITZ_ORE)
        blockWithItem(
            ModBlocks.FLUID_TANK, ExistingModelFile(rl("block/fluid_tank"), this.models().existingFileHelper)
        )

        models().withExistingParent("block/ore_block", ResourceLocation.withDefaultNamespace("block/block"))
            .texture("layer0", ResourceLocation.withDefaultNamespace("block/stone"))
            .texture("layer1", rl("block/ore_overlay")).texture("particle", "#layer0").renderType("cutout").element()
            .cube("#layer0").faces { dir, builder ->
                builder.uvs(0f, 0f, 16f, 16f).cullface(dir).end()
            }.end().element().cube("#layer1").faces { dir, builder ->
                builder.uvs(0f, 0f, 16f, 16f).cullface(dir).tintindex(0).end()
            }.end()

        for (block in ModBlocks.CREATIVE_POWER_SOURCE) {
            val modelRL = ResourceLocation.withDefaultNamespace("block/redstone_block")
            simpleBlock(block.get(), models().getExistingFile(modelRL))
            itemModels().withExistingParent(block.id.path, modelRL)
        }
    }

    private fun blockWithItem(blockRegistryObject: DeferredBlock<*>, model: ModelFile) {
        simpleBlockWithItem(blockRegistryObject.get(), model)
    }

    private fun blockWithItem(blockRegistryObject: DeferredBlock<*>) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()))
    }
}