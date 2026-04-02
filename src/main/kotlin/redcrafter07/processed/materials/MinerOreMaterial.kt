package redcrafter07.processed.materials

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.materials.data.*

class MinerOreMaterial(override val identifier: String, override val color: Int) : MaterialBase, SpaceOreMaterial,
    OreMaterial, DustMaterial, IngotMaterial {
    override var impureDustHolder: DeferredItem<Item>? = null
    override var pureDustHolder: DeferredItem<Item>? = null
    override var washedDustHolder: DeferredItem<Item>? = null
    override var rawHolder: DeferredItem<Item>? = null
    override var dustHolder: DeferredItem<Item>? = null
    override var smallDustHolder: DeferredItem<Item>? = null
    override var ingotHolder: DeferredItem<Item>? = null
    override var nuggetHolder: DeferredItem<Item>? = null
    override var oreItem: DeferredItem<Item>? = null
}