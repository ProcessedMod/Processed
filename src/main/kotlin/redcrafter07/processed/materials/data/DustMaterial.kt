package redcrafter07.processed.materials.data

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedTags
import redcrafter07.processed.materials.getMaybeUnregistered

interface DustMaterial: MaterialBase {
    var dustHolder: DeferredItem<Item>?
    var smallDustHolder: DeferredItem<Item>?

    fun dust(): Item = getMaybeUnregistered(dustHolder)
    fun smallDust(): Item = getMaybeUnregistered(smallDustHolder)

    fun dustTag(): TagKey<Item> = ProcessedTags.Items.commonTag("dusts/$identifier")
    fun smallDustTag(): TagKey<Item> = ProcessedTags.Items.commonTag("small_dusts/$identifier")
}