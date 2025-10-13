package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.items.WrenchItem
import redcrafter07.processed.items.WrenchMode
import redcrafter07.processed.rl

data class WrenchModeChangePacket(val state: WrenchMode) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WrenchModeChangePacket> =
            CustomPacketPayload.Type(rl("p_wrench_change_mode"))
        val CODEC: StreamCodec<ByteBuf, WrenchModeChangePacket> = StreamCodec.composite(
            WrenchMode.STREAM_CODEC, WrenchModeChangePacket::state, ::WrenchModeChangePacket
        )
    }

    fun handleServer(context: IPayloadContext) {
        val item = context.player().mainHandItem
        if (item.item is WrenchItem) WrenchItem.setMode(item, state)
    }

    override fun type(): CustomPacketPayload.Type<WrenchModeChangePacket> = TYPE
}
