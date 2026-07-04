package redcrafter07.processed.materials

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.materials.data.CraftingMaterial
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MaterialBase

class AlloyMaterial(override val identifier: String, override val color: Int) : MaterialBase, DustMaterial, IngotMaterial, CraftingMaterial {
    override var dustHolder: DeferredItem<Item>? = null
    override var smallDustHolder: DeferredItem<Item>? = null
    override var ingotHolder: DeferredItem<Item>? = null
    override var nuggetHolder: DeferredItem<Item>? = null
    override var plateHolder: DeferredItem<Item>? = null
    override var screwHolder: DeferredItem<Item>? = null
    override var rodHolder: DeferredItem<Item>? = null
    override var wiringHolder: DeferredItem<Item>? = null
}