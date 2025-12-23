package redcrafter07.processed.network

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.gui.AbstractFluidContainerMenu
import redcrafter07.processed.rl

class UpdateFluidMenuContentPacket(val containerId: Int, val slot: Int, val content: FluidStack) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<UpdateFluidMenuContentPacket> =
            CustomPacketPayload.Type(rl("update_fluid_menu_content"))
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, UpdateFluidMenuContentPacket> = StreamCodec.composite(
            ByteBufCodecs.INT,
            UpdateFluidMenuContentPacket::containerId,
            ByteBufCodecs.INT,
            UpdateFluidMenuContentPacket::slot,
            FluidStack.OPTIONAL_STREAM_CODEC,
            UpdateFluidMenuContentPacket::content,
            ::UpdateFluidMenuContentPacket
        )
    }

    fun handleClient(context: IPayloadContext) {
        val m = context.player().containerMenu
        if(m.containerId != containerId) return
        if(m !is AbstractFluidContainerMenu) return
        m.setFluid(slot, content)
    }

    override fun type(): CustomPacketPayload.Type<UpdateFluidMenuContentPacket> = TYPE
}