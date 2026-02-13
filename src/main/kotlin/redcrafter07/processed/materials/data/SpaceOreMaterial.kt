package redcrafter07.processed.materials.data

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.materials.getMaybeUnregistered

interface SpaceOreMaterial: MaterialBase, OreMaterial {
    var rawOreItem: DeferredItem<Item>?

    fun rawOre(): Item = getMaybeUnregistered(rawOreItem)
}