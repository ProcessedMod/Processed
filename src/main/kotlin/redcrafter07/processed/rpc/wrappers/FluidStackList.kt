package redcrafter07.processed.rpc.wrappers

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.fluids.FluidStack

class FluidStackList(val inner: List<FluidStack>) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FluidStackList> =
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).map(::FluidStackList, FluidStackList::inner)
    }
}