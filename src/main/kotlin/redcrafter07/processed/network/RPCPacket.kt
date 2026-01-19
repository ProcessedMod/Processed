package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.rl
import redcrafter07.processed.rpc.RpcRegistry

class RPCPacket(val methodName: String, val data: ByteArray) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<RPCPacket> = CustomPacketPayload.Type(rl("rpc_packet"))
        val CODEC: StreamCodec<ByteBuf, RPCPacket> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            RPCPacket::methodName,
            ByteBufCodecs.BYTE_ARRAY,
            RPCPacket::data,
            ::RPCPacket
        )
    }

    fun handle(context: IPayloadContext) {
        RpcRegistry.handle(methodName, data, context)
    }

    override fun type() = TYPE
}