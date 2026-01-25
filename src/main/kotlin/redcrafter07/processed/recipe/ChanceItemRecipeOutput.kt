package redcrafter07.processed.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

class ChanceItemRecipeOutput(val item: ItemStack, val probability: Int) {
    companion object {
        val CODEC: Codec<ChanceItemRecipeOutput> = RecordCodecBuilder.create {
            it.group(
                ItemStack.CODEC.fieldOf("item").forGetter(ChanceItemRecipeOutput::item),
                Codec.INT.fieldOf("probability").forGetter(ChanceItemRecipeOutput::probability)
            ).apply(it, ::ChanceItemRecipeOutput)
        }
        val LIST_CODEC: Codec<List<ChanceItemRecipeOutput>> = Codec.list(CODEC)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ChanceItemRecipeOutput> = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            ChanceItemRecipeOutput::item,
            ByteBufCodecs.INT,
            ChanceItemRecipeOutput::probability,
            ::ChanceItemRecipeOutput
        )
        val LIST_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, List<ChanceItemRecipeOutput>> =
            STREAM_CODEC.apply(ByteBufCodecs.list())
    }
}