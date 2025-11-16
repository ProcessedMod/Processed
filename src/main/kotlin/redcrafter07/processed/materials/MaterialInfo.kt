package redcrafter07.processed.materials

import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import redcrafter07.processed.gui.RenderUtils

class MaterialInfo(
    var types: Types,
    var nuggetVariant: NuggetVariant,
    var rawBlockVariant: RawBlockVariant,
    var color: Int,
    val identifier: String,
    var extraData: MutableList<Lazy<Any>>,
) {
    constructor(identifier: String) : this(
        Types.None, NuggetVariant.LongHoriz, RawBlockVariant.IronLike, 0, identifier, ArrayList()
    )

    fun withExtraData(data: () -> Any): MaterialInfo {
        extraData.add(lazy(data))
        return this
    }

    fun <T> getExtraData(clazz: Class<T>): T? {
        for (data in extraData) {
            @Suppress("UNCHECKED_CAST") if (data.value::class.java == clazz) return data.value as T
        }
        return null
    }

    fun addType(type: Types): MaterialInfo {
        types = types and type
        return this
    }

    fun nuggetVariant(value: NuggetVariant): MaterialInfo {
        nuggetVariant = value
        return this
    }

    fun rawBlockVariant(value: RawBlockVariant): MaterialInfo {
        rawBlockVariant = value
        return this
    }

    fun color(newColor: Int): MaterialInfo {
        color = newColor
        return this
    }

    fun color(r: Int, g: Int, b: Int): MaterialInfo = color(RenderUtils.color(r, g, b))

    fun register(
        materialTag: TagKey<Block> = BlockTags.NEEDS_IRON_TOOL,
        metalBlockProperties: BlockBehaviour.Properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
        oreBlockProperties: BlockBehaviour.Properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)
    ): Material = Materials.register(this, materialTag, metalBlockProperties, oreBlockProperties)

    enum class NuggetVariant(val index: Int) {
        LongHoriz(0), LongVert(1), ShortHoriz(2), ShortVert(3);
    }

    enum class RawBlockVariant(val index: Int) {
        GoldLike(0), IronLike(1);
    }

    // bitmap of material types.
    class Types private constructor(private val value: Int) {
        companion object {
            fun of(bit: Int): Types = Types(1.shl(bit))

            /** Adds an Ore, a Raw Metal, and a Raw Metal Block variant */
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
    }
}