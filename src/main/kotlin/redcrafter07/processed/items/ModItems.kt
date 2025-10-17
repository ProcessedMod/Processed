package redcrafter07.processed.items

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialInfo
import redcrafter07.processed.materials.MaterialItem
import redcrafter07.processed.materials.MaterialItem.Dust
import redcrafter07.processed.materials.MaterialItem.Ingot
import redcrafter07.processed.materials.MaterialItem.Nugget
import redcrafter07.processed.materials.MaterialItem.Raw
import redcrafter07.processed.materials.Materials
import java.util.function.Function
import java.util.function.Supplier

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ProcessedMod.ID)

    val BLITZ_ORB = registerItem("blitz_orb") { ModItem(Item.Properties().stacksTo(64), "blitz_orb") }
    val WRENCH = registerItem("wrench") { WrenchItem() }

    val BASIC_ENGINE = registerItem("engine_tier_0") { EngineItem(ProcessedTier.Basic) }
    val ADVANCED_ENGINE = registerItem("engine_tier_1") { EngineItem(ProcessedTier.Advanced) }

    val BASIC_MINER = registerItem("miner_tier_0") { MinerItem(ProcessedTier.Basic) }
    val ADVANCED_MINER = registerItem("miner_tier_1") { MinerItem(ProcessedTier.Advanced) }

    val DUST_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::dustPath, ::Dust, MaterialInfo.Types.Dust)
    val INGOT_ITEMS =
        registerMaterialItem(Materials.MATERIALS, Material::ingotPath, ::Ingot, MaterialInfo.Types.IngotLike)
    val NUGGET_ITEMS =
        registerMaterialItem(Materials.MATERIALS, Material::nuggetPath, ::Nugget, MaterialInfo.Types.IngotLike)
    val RAW_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::rawPath, ::Raw, MaterialInfo.Types.OreLike)

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