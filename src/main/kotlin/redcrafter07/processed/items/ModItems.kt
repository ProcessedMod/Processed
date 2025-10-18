package redcrafter07.processed.items

import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialInfo
import redcrafter07.processed.materials.MaterialItem
import redcrafter07.processed.materials.MaterialItem.*
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.miner.MinerData
import java.util.function.Function
import java.util.function.Supplier

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ProcessedMod.ID)

    val BLITZ_ORB = registerItem("blitz_orb") { ModItem(Item.Properties().stacksTo(64), "blitz_orb") }
    val WRENCH = registerItem("wrench") { WrenchItem() }

    val LOCATION_SELECTOR = registerItem("location_selector", ::LocationSelectorItem)

    val DUST_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::dustPath, ::Dust, MaterialInfo.Types.Dust)
    val INGOT_ITEMS =
        registerMaterialItem(Materials.MATERIALS, Material::ingotPath, ::Ingot, MaterialInfo.Types.IngotLike)
    val NUGGET_ITEMS =
        registerMaterialItem(Materials.MATERIALS, Material::nuggetPath, ::Nugget, MaterialInfo.Types.IngotLike)
    val RAW_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::rawPath, ::Raw, MaterialInfo.Types.OreLike)

    val ASSEMBLED_MINER = registerItem("assembled_miner", ::AssembledMinerItem)

    init {
        registerWithComponent("hull", ModDataComponents.HULL_DATA, MinerData.Hull(1900, 50000))
        registerWithComponent("tank", ModDataComponents.TANK_DATA, MinerData.Tank(5000, 50000))
        registerWithComponent("engine", ModDataComponents.ENGINE_DATA, MinerData.Engine(50, 20, 2000))
        registerWithComponent("miner", ModDataComponents.MINER_DATA, MinerData.Miners(400, 12, .15f))
        registerWithComponent("cargo_bay", ModDataComponents.CARGO_BAY_DATA, MinerData.CargoBay(300, 7000))
    }

    fun <T> registerWithComponent(name: String, type: Supplier<DataComponentType<T>>, value: T): DeferredItem<Item> =
        registerItem(name) { Item(Item.Properties().component(type, value)) }

    fun <T : Item> registerItem(name: String, item: Supplier<T>): DeferredItem<T> {
        return ITEMS.register(name, item)
    }

    fun <T : MaterialItem> registerMaterialItem(
        materials: List<Material>,
        nameSupplier: Function<Material, String>,
        itemConstructor: Function<Material, T>,
        type: MaterialInfo.Types
    ): List<DeferredItem<T>> {
        val list = ArrayList<DeferredItem<T>>()

        for (material in materials) {
            if (!material.info.types.has(type)) continue
            val name = nameSupplier.apply(material)
            list.add(ITEMS.register(name, Supplier { itemConstructor.apply(material) }))
        }

        return list
    }
}