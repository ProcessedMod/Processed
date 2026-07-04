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
import redcrafter07.processed.materials.data.CraftingMaterial
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
        val washedDusts = this.tag(ProcessedTags.Items.WASHED_DUSTS)
        val pureDusts = this.tag(ProcessedTags.Items.PURE_DUSTS)
        val rawMaterials = this.tag(Tags.Items.RAW_MATERIALS)

        val ores = this.tag(Tags.Items.ORES)

        val plates = this.tag(ProcessedTags.Items.PLATES)
        val screws = this.tag(ProcessedTags.Items.SCREWS)
        val rods = this.tag(Tags.Items.RODS)
        val wiring = this.tag(ProcessedTags.Items.WIRING)

        this.tag(ProcessedTags.Items.INGOT_BLITZ).add(ModItems.BLITZ_ORB.get())

        Materials.getMaterials<DustMaterial>().forEach {
            if (isntVanilla(it.dustHolder)) {
                this.tag(it.dustTag()).add(it.dust())
                dusts.addTag(it.dustTag())
            }
            if (isntVanilla(it.smallDustHolder)) {
                this.tag(it.smallDustTag()).add(it.smallDust())
                smallDusts.addTag(it.smallDustTag())
            }
        }

        Materials.getMaterials<IngotMaterial>().forEach {
            if (isntVanilla(it.ingotHolder)) {
                this.tag(it.ingotTag()).add(it.ingot())
                ingots.addTag(it.ingotTag())
            }
            if (isntVanilla(it.nuggetHolder)) {
                this.tag(it.nuggetTag()).add(it.nugget())
                nuggets.addTag(it.nuggetTag())
            }
        }

        Materials.getMaterials<OreMaterial>().forEach {
            if (isntVanilla(it.impureDustHolder)) {
                this.tag(it.impureDustTag()).add(it.impureDust())
                impureDusts.addTag(it.impureDustTag())
            }
            if (isntVanilla(it.washedDustHolder)) {
                this.tag(it.washedDustTag()).add(it.washedDust())
                washedDusts.addTag(it.washedDustTag())
            }
            if (isntVanilla(it.pureDustHolder)) {
                this.tag(it.pureDustTag()).add(it.pureDust())
                pureDusts.addTag(it.pureDustTag())
            }
            if (isntVanilla(it.rawHolder)) {
                this.tag(it.rawMaterialTag()).add(it.rawMaterial())
                rawMaterials.addTag(it.rawMaterialTag())
            }
        }

        Materials.getMaterials<MinableOreMaterial>().forEach {
            if (isntVanilla(it.oreBlockItemHolder)) {
                this.tag(it.oreBlockItemTag()).add(it.oreBlockItem())
                ores.addTag(it.oreBlockItemTag())
            }
        }

        Materials.getMaterials<CraftingMaterial>().forEach {
            if (isntVanilla(it.rodHolder)) {
                this.tag(it.rodTag()).add(it.rod())
                rods.addTag(it.rodTag())
            }
            if (isntVanilla(it.screwHolder)) {
                this.tag(it.screwTag()).add(it.screw())
                screws.addTag(it.screwTag())
            }
            if (isntVanilla(it.plateHolder)) {
                this.tag(it.plateTag()).add(it.plate())
                plates.addTag(it.plateTag())
            }
            if (isntVanilla(it.wiringHolder)) {
                this.tag(it.wiringTag()).add(it.wiring())
                wiring.addTag(it.wiringTag())
            }
        }
    }
}