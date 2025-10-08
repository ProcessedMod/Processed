package redcrafter07.processed.materials

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.toSubscript

class MaterialInfo(
    var types: Types,
    var nuggetVariant: NuggetVariant,
    var color: Int,
    val identifier: String,
    var chemicalDescription: String
) {
    constructor(identifier: String) : this(Types.None, NuggetVariant.LongHoriz, 0, identifier, "")

    fun addType(type: Types): MaterialInfo {
        types = types and type
        return this
    }

    fun nuggetVariant(value: NuggetVariant): MaterialInfo {
        nuggetVariant = value
        return this
    }

    fun color(newColor: Int): MaterialInfo {
        color = newColor
        return this
    }

    fun color(r: Int, g: Int, b: Int): MaterialInfo = color(RenderUtils.color(r, g, b))

    /** Gets the chemical description based on the makeup of the material */
    fun ofMaterials(vararg materials: Pair<Material, Int>): MaterialInfo {
        var builder = StringBuilder()
        for (material in materials) {
            builder.append(material.first.info.chemicalDescription)
            if (material.second != 1) builder.append(toSubscript(material.second.toString()))
        }
        this.chemicalDescription = builder.toString()
        return this
    }

    fun chemicalDescription(chemicalDescription: String): MaterialInfo {
        this.chemicalDescription = chemicalDescription
        return this
    }

    fun register(
        materialTag: TagKey<Block>,
        metalBlockProperties: BlockBehaviour.Properties,
        oreBlockProperties: BlockBehaviour.Properties
    ): Material = Materials.register(this, materialTag, metalBlockProperties, oreBlockProperties)

    enum class NuggetVariant(val index: Int) {
        LongHoriz(0), LongVert(1), ShortHoriz(2), ShortVert(3);
    }

    // bitmap of material types.
    class Types private constructor(private val value: Int) {
        companion object {
            fun of(bit: Int): Types = Types(1.shl(bit))

            /** Adds an Ore and a Raw Metal variant */
            val OreLike = of(0)

            /** Adds a Dust variant */
            val Dust = of(1)

            /** Adds an Ingot and a Nugget variant */
            val IngotLike = of(2)

            /** Adds a MetalBlock variant */
            val MetalBlock = of(3)

            val None = Types(0)
            val All = OreLike and Dust and IngotLike and MetalBlock
        }

        infix fun and(other: Types): Types = Types(value or other.value)
        fun has(ty: Types) = (value and ty.value) > 0
        val hasOreLike: Boolean get() = has(OreLike)
        val hasDust: Boolean get() = has(Dust)
        val hasIngotLike: Boolean get() = has(IngotLike)
        val hasMetalBlock: Boolean get() = has(MetalBlock)
    }
}