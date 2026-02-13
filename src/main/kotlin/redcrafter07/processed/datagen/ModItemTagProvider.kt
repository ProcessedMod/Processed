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
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.OreMaterial
import redcrafter07.processed.materials.isntVanilla
import java.util.concurrent.CompletableFuture

internal class ModItemTagProvider(
    packOutput: PackOutput,
    providerCompletableFuture: CompletableFuture<HolderLookup.Provider?>,
    tagLookupCompletableFuture: CompletableFuture<TagLookup<Block?>?>,
    existingFileHelper: ExistingFileHelper?
) : ItemTagsProvider(
    packOutput, providerCompletableFuture, tagLookupCompletableFuture, ProcessedMod.ID, existingFileHelper
) {
    override fun addTags(provider: HolderLookup.Provider) {
        val dusts = this.tag(Tags.Items.DUSTS)
        val smallDusts = this.tag(ProcessedTags.Items.SMALL_DUSTS)

        val ingots = this.tag(Tags.Items.INGOTS)
        val nuggets = this.tag(Tags.Items.NUGGETS)

        val impureDusts = this.tag(ProcessedTags.Items.IMPURE_DUSTS)
        val pureDusts = this.tag(ProcessedTags.Items.PURE_DUSTS)
        val rawMaterials = this.tag(Tags.Items.RAW_MATERIALS)

        val ores = this.tag(Tags.Items.ORES)

        this.tag(ProcessedTags.Items.INGOT_BLITZ).add(ModItems.BLITZ_ORB.get())

        Materials.getMaterials<DustMaterial>().forEach {
            if (isntVanilla(it.dustHolder)) {
                this.tag(it.dustTag()).add(it.dust())
                dusts.add(it.dust())
            }
            if (isntVanilla(it.smallDustHolder)) {
                this.tag(it.smallDustTag()).add(it.smallDust())
                smallDusts.add(it.smallDust())
            }
        }

        Materials.getMaterials<IngotMaterial>().forEach {
            if (isntVanilla(it.ingotHolder)) {
                this.tag(it.ingotTag()).add(it.ingot())
                ingots.add(it.ingot())
            }
            if (isntVanilla(it.nuggetHolder)) {
                this.tag(it.nuggetTag()).add(it.nugget())
                nuggets.add(it.nugget())
            }
        }

        Materials.getMaterials<OreMaterial>().forEach {
            if (isntVanilla(it.impureDustHolder)) {
                this.tag(it.impureDustTag()).add(it.impureDust())
                impureDusts.add(it.impureDust())
            }
            if (isntVanilla(it.pureDustHolder)) {
                this.tag(it.pureDustTag()).add(it.pureDust())
                pureDusts.add(it.pureDust())
            }
            if (isntVanilla(it.rawHolder)) {
                this.tag(it.rawMaterialTag()).add(it.rawMaterial())
                rawMaterials.add(it.rawMaterial())
            }
        }

        Materials.getMaterials<MinableOreMaterial>().forEach {
            if(isntVanilla(it.oreBlockItemHolder)) {
                this.tag(it.oreBlockItemTag()).add(it.oreBlockItem())
                ores.add(it.oreBlockItem())
            }
        }
    }
}