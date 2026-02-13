package redcrafter07.processed.materials.data

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.materials.getMaybeUnregistered

interface OreMaterial: MaterialBase {
    var impureDustHolder: DeferredItem<Item>?
    var pureDustHolder: DeferredItem<Item>?
    var rawHolder: DeferredItem<Item>?

    fun impureDust(): Item = getMaybeUnregistered(impureDustHolder)
    fun pureDust(): Item = getMaybeUnregistered(pureDustHolder)
    fun rawMaterial(): Item = getMaybeUnregistered(rawHolder)

    fun impureDustTag(): TagKey<Item> = ProcessedTags.Items.commonTag("impure_dusts/$identifier")
    fun pureDustTag(): TagKey<Item> = ProcessedTags.Items.commonTag("pure_dusts/$identifier")
    fun rawMaterialTag(): TagKey<Item> = ProcessedTags.Items.commonTag("raw_materials/$identifier")
}