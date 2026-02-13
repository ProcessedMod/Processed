package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.FluidTags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.fluid.ModFluids
import java.util.concurrent.CompletableFuture


internal class ModFluidTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider?>,
    existingFileHelper: ExistingFileHelper?
) : FluidTagsProvider(output, lookupProvider, ProcessedMod.ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        // add fuel tags
        for (fluid in ModFluids.REGISTERED_FLUIDS) {
            val tag = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", fluid.type.id.path))
            this.tag(tag).add(fluid.still.get()).add(fluid.flowing.get())
        }
    }
}