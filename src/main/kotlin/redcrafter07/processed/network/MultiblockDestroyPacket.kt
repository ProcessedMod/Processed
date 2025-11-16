package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import redcrafter07.processed.rl
import java.io.IOException

data class MultiblockDestroyPacket(val relPackedPositions: List<Long>, val controllerPos: BlockPos) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<MultiblockDestroyPacket> = CustomPacketPayload.Type(rl("multiblock_destroy"))
        val CODEC: StreamCodec<ByteBuf, MultiblockDestroyPacket> = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()),
            MultiblockDestroyPacket::relPackedPositions,
            BlockPos.STREAM_CODEC,
            MultiblockDestroyPacket::controllerPos,
            ::MultiblockDestroyPacket
        )
    }

    fun handleClient(context: IPayloadContext) {
        try {
            context.player().level().use { level ->
                val x = controllerPos.x
                val y = controllerPos.y
                val z = controllerPos.z
                for (packedPos in relPackedPositions) {
                    val relPos = BlockPos.of(packedPos)
                    val pos = BlockPos(x + relPos.x, y + relPos.y, z + relPos.z)
                    val chunk = level.getChunk(
                        SectionPos.blockToSectionCoord(pos.x),
                        SectionPos.blockToSectionCoord(pos.z),
                        ChunkStatus.FULL,
                        false
                    )
                    if (chunk == null) continue
                    val cache: MultiBlockBlockCache = MultiBlockBlockCache.get(chunk) ?: continue
                    if (!cache.contains(pos)) continue
                    if (cache.getController(pos) != controllerPos) continue
                    cache.removeBlock(pos)
                    level.invalidateCapabilities(pos)
                    cache.set(chunk)
                }
            }
        } catch (_: IOException) {
        } catch (_: UnsupportedOperationException) {
        }
    }

    override fun type(): CustomPacketPayload.Type<MultiblockDestroyPacket> {
        return TYPE
    }
}