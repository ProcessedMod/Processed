package redcrafter07.processed.items

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.Translations
import redcrafter07.processed.materials.MaterialBlockItem
import redcrafter07.processed.materials.MaterialItem
import java.util.function.Supplier

object ModItemGroup {
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessedMod.ID)

    val MAIN_TAB: DeferredHolder<*, *> = CREATIVE_MODE_TABS.register("processed.main", Supplier {
        CreativeModeTab.builder().title(Translations.mainItemGroup())
            .icon { ItemStack(ModItems.LOCATION_SELECTOR.get()) }.displayItems { _, output ->
                for (item in ModItems.ITEMS.entries) {
                    val itemInstance = item.get()
                    if (itemInstance is MaterialItem || itemInstance is MaterialBlockItem) continue
                    output.accept(itemInstance)
                }
            }.build()
    })

    val MATERIALS_TAB: DeferredHolder<*, *> = CREATIVE_MODE_TABS.register("processed.materials", Supplier {
        CreativeModeTab.builder().title(Translations.materialsItemGroup())
            .icon { ItemStack(ModItems.INGOT_ITEMS[2].get()) }.displayItems { _, output ->
                for (item in ModItems.ITEMS.entries) {
                    val itemInstance = item.get()
                    if (itemInstance is MaterialItem || itemInstance is MaterialBlockItem) output.accept(itemInstance)
                }
            }.build()
    })
}