package redcrafter07.processed.datagen

import net.neoforged.neoforge.common.data.LanguageProvider
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.CraftingMaterial
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.OreMaterial
import redcrafter07.processed.materials.isntVanilla

object TagsLanguageProvider {
    fun addTranslations(
        langProvider: LanguageProvider,
        dustFmt: String,
        smallDustFmt: String,
        impureDustFmt: String,
        pureDustFmt: String,
        washedDustFmt: String,
        ingotFmt: String,
        nuggetFmt: String,
        oresFmt: String,
        rawMaterialsFmt: String,
        plateFmt: String,
        screwFmt: String,
        rodFmt: String,
        wiringFmt: String,
        materialNames: (String) -> String,
    ) {
        Materials.MATERIALS.forEach {
            val name = materialNames(it.identifier)
            langProvider.add("processed.material.${it.identifier}", name)

            if (it is DustMaterial) {
                if (isntVanilla(it.dustHolder)) langProvider.addTag(it::dustTag, dustFmt.format(name))
                langProvider.addTag(it::smallDustTag, smallDustFmt.format(name))
            }

            if (it is IngotMaterial) {
                if (isntVanilla(it.ingotHolder)) langProvider.addTag(it::ingotTag, ingotFmt.format(name))
                if (isntVanilla(it.nuggetHolder)) langProvider.addTag(it::nuggetTag, nuggetFmt.format(name))
            }

            if (it is OreMaterial) {
                langProvider.addTag(it::impureDustTag, impureDustFmt.format(name))
                langProvider.addTag(it::washedDustTag, washedDustFmt.format(name))
                langProvider.addTag(it::pureDustTag, pureDustFmt.format(name))
                if (isntVanilla(it.rawHolder)) langProvider.addTag(it::rawMaterialTag, rawMaterialsFmt.format(name))
            }

            if (it is MinableOreMaterial) langProvider.addTag(it::oreBlockItemTag, oresFmt.format(name))

            if (it is CraftingMaterial) {
                langProvider.addTag(it::plateTag, plateFmt.format(name))
                langProvider.addTag(it::screwTag, screwFmt.format(name))
                langProvider.addTag(it::rodTag, rodFmt.format(name))
                langProvider.addTag(it::wiringTag, wiringFmt.format(name))
            }
        }
    }

}