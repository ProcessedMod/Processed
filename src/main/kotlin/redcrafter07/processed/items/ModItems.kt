package redcrafter07.processed.items

import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.materials.MaterialItem.*
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.*
import redcrafter07.processed.materials.isntVanilla
import redcrafter07.processed.miner.MinerData
import redcrafter07.processed.rl
import java.util.function.Function
import java.util.function.Supplier
import kotlin.reflect.KMutableProperty1

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ProcessedMod.ID)

    val BLITZ_ORB = registerItem("blitz_orb") { ModItem(Item.Properties().stacksTo(64), "blitz_orb") }
    val WRENCH = registerItem("wrench") { WrenchItem() }

    val LOCATION_SELECTOR = registerItem("location_selector", ::LocationSelectorItem)

    val DUST_ITEMS = registerMaterialItems(DustMaterial::dustHolder, ::Dust) { it.identifier + "_dust" }
    val SMALL_DUST_ITEMS =
        registerMaterialItems(DustMaterial::smallDustHolder, ::SmallDust) { "small_${it.identifier}_dust" }

    val INGOT_ITEMS = registerMaterialItems(IngotMaterial::ingotHolder, ::Ingot) { it.identifier + "_ingot" }
    val NUGGET_ITEMS = registerMaterialItems(IngotMaterial::nuggetHolder, ::Nugget) { it.identifier + "_nugget" }

    val IMPURE_DUST_ITEMS =
        registerMaterialItems(OreMaterial::impureDustHolder, ::ImpureDust) { "impure_${it.identifier}_dust" }
    val PURE_DUST_ITEMS =
        registerMaterialItems(OreMaterial::pureDustHolder, ::PureDust) { "pure_${it.identifier}_dust" }
    val WASHED_DUST_ITEMS =
        registerMaterialItems(OreMaterial::washedDustHolder, ::WashedDust) { "washed_${it.identifier}_dust" }
    val RAW_MATERIAL_ITEMS = registerMaterialItems(OreMaterial::rawHolder, ::Raw) { "raw_${it.identifier}" }

    val SPACE_ORE_ITEMS = registerMaterialItems(SpaceOreMaterial::oreItem, ::SpaceOre) { "${it.identifier}_ore" }

    val PLATE_ITEMS = registerMaterialItems(CraftingMaterial::plateHolder, ::Plate) { it.identifier + "_plate" }
    val ROD_ITEMS = registerMaterialItems(CraftingMaterial::rodHolder, ::Rod) { it.identifier + "_rod" }
    val SCREW_ITEMS = registerMaterialItems(CraftingMaterial::screwHolder, ::Screw) { it.identifier + "_screw" }
    val WIRING_ITEMS = registerMaterialItems(CraftingMaterial::wiringHolder, ::Wiring) { it.identifier + "_wiring" }

    val MASS_SPECTROMETRY_DATA = registerItem("mass_spectrometry_data", ::ResearchContainingItem)
    val MATERIAL_ANALYSIS = registerItem("material_analysis", ::ResearchContainingItem)

    val ASSEMBLED_MINER = registerItem("assembled_mining_rocket", ::AssembledMinerItem)
    val SPACE_HULL = registerWithComponent("space_hull", ModDataComponents.HULL_DATA, MinerData.Hull(1, 5000000))

    val SPACE_TANK = registerWithComponent("space_tank", ModDataComponents.TANK_DATA, MinerData.Tank(1, 10000))
    val SPACE_ENGINE = registerWithComponent(
        "space_engine", ModDataComponents.ENGINE_DATA, MinerData.Engine(
            1, 15000000, 200, rl("fuel")
        )
    )
    val SPACE_MINER = registerWithComponent("space_miner", ModDataComponents.MINER_DATA, MinerData.Miners(1, 32))
    val SPACE_CARGO_BAY =
        registerWithComponent("space_cargo_bay", ModDataComponents.CARGO_BAY_DATA, MinerData.CargoBay(1, 128))

    fun <T> registerWithComponent(name: String, type: Supplier<DataComponentType<T>>, value: T): DeferredItem<Item> =
        registerItem(name) { Item(Item.Properties().component(type, value)) }

    fun <T : Item> registerItem(name: String, item: Supplier<T>): DeferredItem<T> {
        return ITEMS.register(name, item)
    }

    inline fun <reified T : MaterialBase> registerMaterialItems(
        property: KMutableProperty1<T, DeferredItem<Item>?>,
        itemConstructor: Function<T, Item>,
        name: Function<T, String>,
    ): List<DeferredItem<Item>> = Materials.getMaterials<T>().filter { isntVanilla(property.get(it)) }.map {
        val item = ITEMS.register(name.apply(it), Supplier { itemConstructor.apply(it) })
        property.set(it, item)
        item
    }.toList()
}