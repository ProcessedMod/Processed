package redcrafter07.processed.items

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialItem
import redcrafter07.processed.materials.Materials
import java.util.function.Function
import java.util.function.Supplier

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ProcessedMod.ID)

    val BLITZ_ORB = registerItem("blitz_orb") { ModItem(Item.Properties().stacksTo(64), "blitz_orb") }
    val WRENCH = registerItem("wrench") { WrenchItem() }

    val DUST_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::dustPath) { MaterialItem.Dust(it) }
    val INGOT_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::ingotPath) { MaterialItem.Ingot(it) }
    val NUGGET_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::nuggetPath) { MaterialItem.Nugget(it) }
    val RAW_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::rawPath) { MaterialItem.Raw(it) }

    fun <T : Item> registerItem(name: String, item: Supplier<T>): DeferredItem<T> {
        return ITEMS.register(name, item)
    }

    fun <T : MaterialItem> registerMaterialItem(
        materials: List<Material>, nameSupplier: Function<Material, String>, itemConstructor: Function<Material, T>
    ): List<DeferredItem<T>> {
        val list = ArrayList<DeferredItem<T>>()

        for (material in materials) {
            val name = nameSupplier.apply(material)
            list.add(ITEMS.register(name, Supplier { itemConstructor.apply(material) }))
        }

        return list
    }
}