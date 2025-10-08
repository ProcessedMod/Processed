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
    public override fun registerModels() {
        simpleItem(ModItems.BLITZ_ORB)
        simpleItem(ModItems.WRENCH)

        for (dustItem in ModItems.DUST_ITEMS) {
            withExistingParent(
                dustItem.id.path,
                ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0", rl("item/dust"))
        }

        for (dustItem in ModItems.INGOT_ITEMS) {
            withExistingParent(
                dustItem.id.path,
                ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0", rl("item/ingot"))
        }

        for (dustItem in ModItems.NUGGET_ITEMS) {
            withExistingParent(
                dustItem.id.path,
                ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0", rl("item/nugget"))
        }

        for (dustItem in ModItems.RAW_ITEMS) {
            withExistingParent(
                dustItem.id.path,
                ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0", rl("item/raw_metal"))
        }
    }

    private fun simpleItem(item: DeferredItem<*>) {
        withExistingParent(
            item.getId().getPath(),
            ResourceLocation.withDefaultNamespace("item/generated")
        ).texture("layer0", rl("item/${item.id.path}"))
    }
}