package redcrafter07.processed.materials.data

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.materials.getMaybeUnregistered

interface IngotMaterial: MaterialBase {
    var ingotHolder: DeferredItem<Item>?
    var nuggetHolder: DeferredItem<Item>?

    fun ingot(): Item = getMaybeUnregistered(ingotHolder)
    fun nugget(): Item = getMaybeUnregistered(nuggetHolder)

    fun ingotTag(): TagKey<Item> = ProcessedTags.Items.commonTag("ingots/$identifier")
    fun nuggetTag(): TagKey<Item> = ProcessedTags.Items.commonTag("nuggets/$identifier")
}