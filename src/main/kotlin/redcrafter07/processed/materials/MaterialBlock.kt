package redcrafter07.processed.materials

import net.minecraft.world.level.block.Block

abstract class MaterialBlock(override val material: Material, itemProperties: Properties): Block(itemProperties), MaterialContainer {
    class MetalBlock(material: Material): MaterialBlock(material, material.metalBlockProperties)
    class OreBlock(material: Material): MaterialBlock(material, material.oreBlockProperties)
}