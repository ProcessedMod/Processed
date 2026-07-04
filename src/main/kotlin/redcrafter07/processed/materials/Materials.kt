package redcrafter07.processed.materials

import redcrafter07.processed.gui.RenderUtils.color
import redcrafter07.processed.materials.data.MaterialBase
import java.util.stream.Stream

object Materials {
    val MATERIALS = ArrayList<MaterialBase>()

    val ALUMINIUM = register(MineOreMaterial("aluminium", color(0xd0, 0xd5, 0xd9)))
    val TIN = register(MineOreMaterial("tin", color(0xd5, 0xd6, 0xbc)))
    val NICKEL = register(MineOreMaterial("nickel", color(0x4d, 0xd4, 0xa9)))
    val TITANIUM = register(MineOreMaterial("titanium", color(0xcf, 0x71, 0xaf)))
    val URANIUM = register(MineOreMaterial("uranium", color(0x3c, 0xff, 0x49)))
    val NAQUADAH = register(MinerOreMaterial("naquadah", color(0x1f, 0x30, 0x21)))
    val IRON = register(IronMaterial)
    val COPPER = register(CopperMaterial)

    val STEEL = register(AlloyMaterial("steel", color(0x49, 0x4b, 0x4d)))
    val NICKEL_TITANIUM = register(AlloyMaterial("nickel_titanium", color(0x8f, 0xa3, 0xa9)))

    fun <T : MaterialBase> register(material: T) = material.apply { MATERIALS.add(this) }

    inline fun <reified T> getMaterials(): Stream<T> =
        MATERIALS.stream().mapMulti { v, consumer -> if (v is T) consumer.accept(v) }
}