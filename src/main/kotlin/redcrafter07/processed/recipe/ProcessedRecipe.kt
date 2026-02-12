package redcrafter07.processed.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.NonNullList
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import redcrafter07.processed.ProcessedTier

abstract class ProcessedRecipe<Input : RecipeInput>(
    val energyUsage: Int, processingTime: Int, val baseTier: ProcessedTier
) : Recipe<TieredInput<Input>> {
    companion object {
        fun <B : ByteBuf, C : ProcessedRecipe<*>, T1, T2, T3> streamComp(
            c1: StreamCodec<B, T1>,
            g1: (C) -> T1,
            c2: StreamCodec<B, T2>,
            g2: (C) -> T2,
            c3: StreamCodec<B, T3>,
            g3: (C) -> T3,
            ctor: (T1, T2, T3, Int, Int, ProcessedTier) -> C
        ): StreamCodec<B, C> {
            return object : StreamCodec<B, C> {
                override fun decode(buffer: B): C {
                    val v1 = c1.decode(buffer)
                    val v2 = c2.decode(buffer)
                    val v3 = c3.decode(buffer)
                    val energyUsage = buffer.readInt()
                    val processingTime = buffer.readInt()
                    val baseTier = ProcessedTier.STREAM_CODEC.decode(buffer)
                    return ctor(v1, v2, v3, energyUsage, processingTime, baseTier)
                }

                override fun encode(buffer: B, value: C) {
                    c1.encode(buffer, g1(value)!!)
                    c2.encode(buffer, g2(value)!!)
                    c3.encode(buffer, g3(value)!!)
                    buffer.writeInt(value.energyUsage)
                    buffer.writeInt(value.processingTime)
                    ProcessedTier.STREAM_CODEC.encode(buffer, value.baseTier)
                }
            }
        }
        fun <B : ByteBuf, C : ProcessedRecipe<*>, T1, T2, T3, T4> streamComp(
            c1: StreamCodec<B, T1>,
            g1: (C) -> T1,
            c2: StreamCodec<B, T2>,
            g2: (C) -> T2,
            c3: StreamCodec<B, T3>,
            g3: (C) -> T3,
            c4: StreamCodec<B, T4>,
            g4: (C) -> T4,
            ctor: (T1, T2, T3, T4, Int, Int, ProcessedTier) -> C
        ): StreamCodec<B, C> {
            return object : StreamCodec<B, C> {
                override fun decode(buffer: B): C {
                    val v1 = c1.decode(buffer)
                    val v2 = c2.decode(buffer)
                    val v3 = c3.decode(buffer)
                    val v4 = c4.decode(buffer)
                    val energyUsage = buffer.readInt()
                    val processingTime = buffer.readInt()
                    val baseTier = ProcessedTier.STREAM_CODEC.decode(buffer)
                    return ctor(v1, v2, v3, v4, energyUsage, processingTime, baseTier)
                }

                override fun encode(buffer: B, value: C) {
                    c1.encode(buffer, g1(value)!!)
                    c2.encode(buffer, g2(value)!!)
                    c3.encode(buffer, g3(value)!!)
                    c4.encode(buffer, g4(value)!!)
                    buffer.writeInt(value.energyUsage)
                    buffer.writeInt(value.processingTime)
                    ProcessedTier.STREAM_CODEC.encode(buffer, value.baseTier)
                }
            }
        }

        fun <C : ProcessedRecipe<*>> energy(): RecordCodecBuilder<C, Int> =
            Codec.INT.fieldOf("energyUsage").forGetter(ProcessedRecipe<*>::energyUsage)

        fun <C : ProcessedRecipe<*>> processing(): RecordCodecBuilder<C, Int> =
            Codec.INT.fieldOf("processingTime").forGetter(ProcessedRecipe<*>::processingTime)

        fun <C : ProcessedRecipe<*>> tier(): RecordCodecBuilder<C, ProcessedTier> =
            ProcessedTier.CODEC.fieldOf("baseTier").forGetter(ProcessedRecipe<*>::baseTier)
    }

    @Deprecated(
        message = "getRemainingItems without a level or random source is deprecated.",
        replaceWith = ReplaceWith("getRemainingItems")
    )
    final override fun getRemainingItems(input: TieredInput<Input>): NonNullList<ItemStack> =
        super.getRemainingItems(input)

    open fun getRemainingItems(input: TieredInput<Input>, level: Level) = getRemainingItems(input, level.random)
    abstract fun getRemainingItems(input: TieredInput<Input>, random: RandomSource): NonNullList<ItemStack>

    val processingTime = processingTime * baseTier.speedMultiplier
}