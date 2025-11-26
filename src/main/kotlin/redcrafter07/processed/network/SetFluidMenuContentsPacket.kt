package redcrafter07.processed.network

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.gui.AbstractFluidContainerMenu
import redcrafter07.processed.rl

class SetFluidMenuContentsPacket(val containerId: Int, val fluidSlots: List<FluidStack>) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<SetFluidMenuContentsPacket> =
            CustomPacketPayload.Type(rl("set_fluid_menu_contents"))
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, SetFluidMenuContentsPacket> = StreamCodec.composite(
            ByteBufCodecs.INT,
            SetFluidMenuContentsPacket::containerId,
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
            SetFluidMenuContentsPacket::fluidSlots,
            ::SetFluidMenuContentsPacket
        )
    }

    fun handleClient(context: IPayloadContext) {
        val m = context.player().containerMenu
        if(m.containerId != containerId) return
        if(m !is AbstractFluidContainerMenu) return
        for (i in 0..<fluidSlots.size) m.setFluid(i, fluidSlots[i])
    }

    override fun type(): CustomPacketPayload.Type<SetFluidMenuContentsPacket> = TYPE
}