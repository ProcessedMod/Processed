package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.ModBlocks
import kotlin.collections.contains
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.level.block.Block as McBlock

interface Part {
    fun blockType(state: BlockState, level: LevelAccessor, pos: BlockPos): MultiblockBlockEntity.SpecialBlockType?
    fun matchingBlocks(regs: RegistryAccess): List<BlockState>

    fun or(vararg other: Part) = if (this == Empty || other.contains(Empty)) Empty else Union(this, *other)
    fun itemInput() = SpecialBlock(this, MultiblockBlockEntity.SpecialBlockType.ItemInput)
    fun itemOutput() = SpecialBlock(this, MultiblockBlockEntity.SpecialBlockType.ItemOutput)
    fun fluidInput() = SpecialBlock(this, MultiblockBlockEntity.SpecialBlockType.FluidInput)
    fun fluidOutput() = SpecialBlock(this, MultiblockBlockEntity.SpecialBlockType.FluidOutput)
    fun energyInput() = SpecialBlock(this, MultiblockBlockEntity.SpecialBlockType.EnergyInput)

    infix fun or(other: Part): Part = if (this == Empty || other == Empty) Empty else Union(this, other)

    companion object {
        fun block(rl: ResourceLocation) = Block(rl)
        fun block(key: ResourceKey<McBlock>) = Block(key)
        fun block(holder: Holder<McBlock>) = Block(holder)
        fun block(block: McBlock) = Block(block)
        fun blocks(holders: List<Holder<McBlock>>) = Union(holders.map(::Block).toMutableList())
        fun tag(tag: TagKey<McBlock>) = Tag(tag)
        fun air() = block(Blocks.AIR)
        fun ignored() = Empty
        fun controller() = Controller

        val ITEM_IN = block(ModBlocks.ITEM_INPUT_HATCH).itemInput()
        val ITEM_OUT = block(ModBlocks.ITEM_OUTPUT_HATCH).itemOutput()
        val FLUID_IN = block(ModBlocks.FLUID_INPUT_HATCH).fluidInput()
        val FLUID_OUT = block(ModBlocks.FLUID_OUTPUT_HATCH).fluidOutput()
        val ENERGY_IN = blocks(ModBlocks.ENERGY_HATCHES.toList()).energyInput()
    }

    class SpecialBlock(val inner: Part, val type: MultiblockBlockEntity.SpecialBlockType) : Part {
        override fun blockType(state: BlockState, level: LevelAccessor, pos: BlockPos) =
            inner.blockType(state, level, pos)?.run { type }

        override fun matchingBlocks(regs: RegistryAccess) = inner.matchingBlocks(regs)

        override fun toString() = "SpecialBlock($inner, $type)"
    }

    object Empty : Part {
        override fun blockType(state: BlockState, level: LevelAccessor, pos: BlockPos) =
            MultiblockBlockEntity.SpecialBlockType.Ignored

        override fun matchingBlocks(regs: RegistryAccess) = listOf<BlockState>()

        override fun or(vararg other: Part) = this
        override infix fun or(other: Part) = this
        override fun toString() = "any"
    }

    object Controller : Part {
        override fun blockType(
            state: BlockState, level: LevelAccessor, pos: BlockPos
        ) = throw IllegalStateException("Part.Controller's blockType called")

        override fun matchingBlocks(regs: RegistryAccess) = listOf<BlockState>()

        override fun or(vararg other: Part) = throw IllegalStateException("Part.Controller's or called")
        override infix fun or(other: Part) = throw IllegalStateException("Part.Controller's or called")
        override fun toString() = "controller"
    }

    class Union(var parts: MutableList<Part>?) : Part {
        constructor(vararg parts: Part) : this(parts.toMutableList())

        override fun matchingBlocks(regs: RegistryAccess) = parts?.flatMap { it.matchingBlocks(regs) } ?: listOf()

        init {
            val parts = parts
            if (parts != null) {
                for (part in parts) if (part == Controller) throw IllegalStateException("Union contains a controller")
                if (parts.contains(Empty)) this.parts = null
            }
        }

        override fun blockType(
            state: BlockState, level: LevelAccessor, pos: BlockPos
        ): MultiblockBlockEntity.SpecialBlockType? {
            val parts = parts
            if (parts.isNullOrEmpty()) return MultiblockBlockEntity.SpecialBlockType.Ignored
            for (part in parts) {
                val ty = part.blockType(state, level, pos)
                if (ty != null) return ty
            }
            return null
        }

        override fun or(vararg other: Part): Part {
            val parts = parts ?: return Empty
            if (other.contains(Empty)) {
                this.parts = null
                return Empty
            }
            parts.addAll(other)
            return this
        }

        override infix fun or(other: Part): Part {
            val parts = parts ?: return Empty
            if (other == Empty) {
                this.parts = null
                return Empty
            }
            parts.add(other)
            return this
        }

        override fun toString(): String {
            val parts = parts ?: return "any"
            if (parts.isEmpty()) return "any"
            else if (parts.size == 1) return parts[0].toString()
            val builder = StringBuilder(parts[0].toString())
            for (i in 1..<parts.size) builder.append(" | ").append(parts[i].toString())
            return builder.toString()
        }
    }

    class Block private constructor(val block: Any) : Part {
        constructor(rl: ResourceLocation) : this(rl as Any)
        constructor(key: ResourceKey<McBlock>) : this(key as Any)
        constructor(holder: Holder<McBlock>) : this(holder as Any)
        constructor(block: McBlock) : this(block as Any)

        @Suppress("UNCHECKED_CAST")
        override fun blockType(state: BlockState, level: LevelAccessor, pos: BlockPos) = when (block) {
            is ResourceLocation -> state.blockHolder.`is`(block)
            is ResourceKey<*> -> state.`is`(block as ResourceKey<McBlock>)
            is Holder<*> -> state.`is`(block as Holder<McBlock>)
            is McBlock -> state.`is`(block)
            else -> throw IllegalStateException()
        }.run { if (this) MultiblockBlockEntity.SpecialBlockType.None else null }

        override fun matchingBlocks(regs: RegistryAccess) = when (block) {
            is ResourceLocation -> listOf(BuiltInRegistries.BLOCK.get(block).defaultBlockState())
            is ResourceKey<*> -> listOf(BuiltInRegistries.BLOCK.get(block.location()).defaultBlockState())
            is Holder<*> -> listOf((block.value() as McBlock).defaultBlockState())
            is McBlock -> listOf(block.defaultBlockState())
            else -> throw IllegalStateException()
        }

        @Suppress("DEPRECATION")
        override fun toString() = when (block) {
            is ResourceLocation -> block.toString()
            is ResourceKey<*> -> block.location().toString()
            is Holder<*> -> {
                val key = block.unwrapKey()
                if (key.isPresent) key.get().location().toString()
                else (block.value() as McBlock).builtInRegistryHolder().unwrapKey().get().location().toString()
            }

            is McBlock -> block.builtInRegistryHolder().unwrapKey().get().location().toString()
            else -> throw IllegalStateException()
        }
    }

    class Tag(val tag: TagKey<McBlock>) : Part {
        override fun blockType(state: BlockState, level: LevelAccessor, pos: BlockPos) =
            if (state.`is`(tag)) MultiblockBlockEntity.SpecialBlockType.None else null

        override fun matchingBlocks(regs: RegistryAccess): List<BlockState> {
            val reg = regs.registry(tag.registry).getOrNull() ?: return listOf()
            val contents = reg.getTag(tag).getOrNull() ?: return listOf()
            return contents.stream().map { it.value().defaultBlockState() }.toList()
        }

        override fun toString() = "#${tag.location}"
    }
}