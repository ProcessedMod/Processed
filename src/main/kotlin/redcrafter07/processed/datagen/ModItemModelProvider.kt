package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.covers.covers.ModCovers
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, ProcessedMod.ID, existingFileHelper) {
    val generatedItemModel: ResourceLocation = ResourceLocation.withDefaultNamespace("item/generated")

    public override fun registerModels() {
        simpleItem(ModItems.BLITZ_ORB)
        simpleItem(ModItems.WRENCH)
        simpleItem(ModItems.LOCATION_SELECTOR)
        simpleItem(ModItems.MASS_SPECTROMETRY_DATA)
        simpleItem(ModItems.MATERIAL_ANALYSIS)

        simpleModel("dust_item", "item/dust")
        simpleModel("impure_dust_item", "item/dust", "item/impure_dust_overlay")
        simpleModel("washed_dust_item", "item/dust", "item/washed_dust_overlay")
        simpleModel("pure_dust_item", "item/dust", "item/pure_dust_overlay")
        simpleModel("space_ore", "item/space_ore", "item/space_ore_overlay")
        simpleModel("small_dust_item", "item/small_dust")
        simpleModel("ingot_item", "item/ingot")
        simpleModel("nugget_item", "item/nugget")
        simpleModel("raw_item", "item/raw_metal")
        simpleModel("plate", "item/components/plate")
        simpleModel("rod", "item/components/rod")
        simpleModel("screw", "item/components/screw")
        simpleModel("wiring", "item/components/wiring")
        ModCovers.COVERS.entries.forEach { def -> simpleItem(def.get().location.value) }
    }

    private fun simpleItem(item: DeferredItem<*>) {
        withExistingParent(
            item.id.path, generatedItemModel
        ).texture("layer0", rl("item/${item.id.path}"))
    }

    private fun simpleItem(item: ResourceLocation) {
        withExistingParent(
            item.path, generatedItemModel
        ).texture("layer0", item.withPrefix("item/"))
    }

    private fun simpleModel(path: String, texture: String): ResourceLocation =
        withExistingParent(path, generatedItemModel).texture("layer0", rl(texture)).location

    private fun simpleModel(path: String, layer0: String, layer1: String): ResourceLocation =
        withExistingParent(path, generatedItemModel).texture("layer0", rl(layer0))
            .texture("layer1", rl(layer1)).location
}