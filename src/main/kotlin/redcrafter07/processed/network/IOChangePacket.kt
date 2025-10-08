package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.rl
import java.io.IOException

data class IOChangePacket(val block: BlockPos, val state: IoState, val side: BlockSide, val itemOrFluid: Boolean) :
    CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<IOChangePacket> = CustomPacketPayload.Type(rl("p_io_change"))
        val CODEC: StreamCodec<ByteBuf, IOChangePacket> = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            IOChangePacket::block,
            IoState.STREAM_CODEC,
            IOChangePacket::state,
            BlockSide.STREAM_CODEC,
            IOChangePacket::side,
            ByteBufCodecs.BOOL,
            IOChangePacket::itemOrFluid,
            ::IOChangePacket
        )
    }

    override fun type(): CustomPacketPayload.Type<IOChangePacket> = TYPE

    fun handleServer(context: IPayloadContext) {
        try {
            val level = context.player().level()
            val blockEntity = level.getBlockEntity(block)
            if (blockEntity is ProcessedMachine) {
                blockEntity.setSide(itemOrFluid, side, state)
                blockEntity.invalidateCapabilities()
            }
        } catch (_: IOException) {
        }
    }
}
