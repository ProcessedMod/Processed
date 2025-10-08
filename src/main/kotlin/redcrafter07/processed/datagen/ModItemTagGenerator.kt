package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.items.ModItems
import java.util.concurrent.CompletableFuture

internal class ModItemTagGenerator(
    packOutput: PackOutput,
    providerCompletableFuture: CompletableFuture<HolderLookup.Provider?>,
    tagLookupCompletableFuture: CompletableFuture<TagLookup<Block?>?>,
    existingFileHelper: ExistingFileHelper?
) : ItemTagsProvider(
    packOutput,
    providerCompletableFuture,
    tagLookupCompletableFuture,
    ProcessedMod.ID,
    existingFileHelper
) {
    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(Tags.Items.INGOTS)
            .add(ModItems.BLITZ_ORB.get())
            .add(*ModItems.INGOT_ITEMS.stream().map { it.get() }.toList().toTypedArray())

        this.tag(Tags.Items.DUSTS)
            .add(*ModItems.DUST_ITEMS.stream().map { it.get() }.toList().toTypedArray())

        this.tag(Tags.Items.NUGGETS)
            .add(*ModItems.NUGGET_ITEMS.stream().map { it.get() }.toList().toTypedArray())

        this.tag(ProcessedTags.Items.INGOT_BLITZ)
            .add(ModItems.BLITZ_ORB.get())


        for (item in ModItems.DUST_ITEMS) this.tag(item.get().tag).add(item.get())

        for (item in ModItems.INGOT_ITEMS) this.tag(item.get().tag).add(item.get())

        for (item in ModItems.NUGGET_ITEMS) this.tag(item.get().tag).add(item.get())
    }
}