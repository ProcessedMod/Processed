package redcrafter07.processed.materials.data

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.materials.getMaybeUnregistered

interface CraftingMaterial: MaterialBase {
    var plateHolder: DeferredItem<Item>?
    var screwHolder: DeferredItem<Item>?
    var rodHolder: DeferredItem<Item>?
    var wiringHolder: DeferredItem<Item>?

    fun plate(): Item = getMaybeUnregistered(plateHolder)
    fun screw(): Item = getMaybeUnregistered(screwHolder)
    fun rod(): Item = getMaybeUnregistered(rodHolder)
    fun wiring(): Item = getMaybeUnregistered(wiringHolder)

    fun plateTag(): TagKey<Item> = ProcessedTags.Items.commonTag("plates/$identifier")
    fun screwTag(): TagKey<Item> = ProcessedTags.Items.commonTag("screws/$identifier")
    fun rodTag(): TagKey<Item> = ProcessedTags.Items.commonTag("rods/$identifier")
    fun wiringTag(): TagKey<Item> = ProcessedTags.Items.commonTag("wiring/$identifier")
}