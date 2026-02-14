package redcrafter07.processed.materials

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.OreMaterial

class MineOreMaterial(override val identifier: String, override val color: Int) : MaterialBase, MinableOreMaterial,
    OreMaterial, DustMaterial, IngotMaterial {
    override var oreBlockHolder: DeferredBlock<Block>? = null
    override var oreBlockItemHolder: DeferredItem<Item>? = null
    override var impureDustHolder: DeferredItem<Item>? = null
    override var pureDustHolder: DeferredItem<Item>? = null
    override var washedDustHolder: DeferredItem<Item>? = null
    override var rawHolder: DeferredItem<Item>? = null
    override var dustHolder: DeferredItem<Item>? = null
    override var smallDustHolder: DeferredItem<Item>? = null
    override var ingotHolder: DeferredItem<Item>? = null
    override var nuggetHolder: DeferredItem<Item>? = null
}