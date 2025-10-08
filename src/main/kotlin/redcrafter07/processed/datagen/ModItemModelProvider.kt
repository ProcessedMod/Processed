package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, ProcessedMod.ID, existingFileHelper) {
    val generatedItemModel: ResourceLocation = ResourceLocation.withDefaultNamespace("item/generated")

    public override fun registerModels() {
        simpleItem(ModItems.BLITZ_ORB)
        simpleItem(ModItems.WRENCH)

        val dustModel = simpleModel("dust_item", "item/dust")
        val ingotModel = simpleModel("ingot_item", "item/ingot")
        val nuggetModel = simpleModel("nugget_item", "item/nugget")
        val rawModel = simpleModel("raw_item", "item/raw_metal")

        for (dustItem in ModItems.DUST_ITEMS) withExistingParent(dustItem.id.path, dustModel)
        for (dustItem in ModItems.INGOT_ITEMS) withExistingParent(dustItem.id.path, ingotModel)
        for (dustItem in ModItems.NUGGET_ITEMS) withExistingParent(dustItem.id.path, nuggetModel)
        for (dustItem in ModItems.RAW_ITEMS) withExistingParent(dustItem.id.path, rawModel)
    }

    private fun simpleItem(item: DeferredItem<*>) {
        withExistingParent(
            item.id.path, generatedItemModel
        ).texture("layer0", rl("item/${item.id.path}"))
    }

    private fun simpleModel(path: String, texture: String): ResourceLocation =
        withExistingParent(path, generatedItemModel).texture("layer0", rl(texture)).location
}