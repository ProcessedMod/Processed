package redcrafter07.processed.materials

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import redcrafter07.processed.materials.data.MaterialBase

abstract class MaterialBlock(override val material: MaterialBase, itemProperties: Properties) : Block(itemProperties),
    MaterialContainer {
    class OreBlock(material: MaterialBase) : MaterialBlock(material, Properties.ofFullCopy(Blocks.IRON_ORE))
}