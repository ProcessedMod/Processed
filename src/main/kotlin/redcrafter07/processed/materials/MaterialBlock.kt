package redcrafter07.processed.materials

import net.minecraft.world.level.block.Block

abstract class MaterialBlock(val material: Material, itemProperties: Properties): Block(itemProperties) {
    class MetalBlock(material: Material): MaterialBlock(material, material.metalBlockProperties)
    class OreBlock(material: Material): MaterialBlock(material, material.oreBlockProperties)
}