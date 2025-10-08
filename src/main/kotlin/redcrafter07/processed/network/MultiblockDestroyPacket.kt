package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.multiblock.MultiBlockCasingCache
import redcrafter07.processed.rl
import java.io.IOException

data class MultiblockDestroyPacket(val positions: List<BlockPos>, val controllerPos: BlockPos) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<MultiblockDestroyPacket> = CustomPacketPayload.Type(rl("multiblock_destroy"))
        val CODEC: StreamCodec<ByteBuf, MultiblockDestroyPacket> = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MultiblockDestroyPacket::positions,
            BlockPos.STREAM_CODEC,
            MultiblockDestroyPacket::controllerPos,
            ::MultiblockDestroyPacket
        )
    }

    fun handleClient(context: IPayloadContext) {
        try {
            context.player().level().use { level ->
                for (position in positions) {
                    val chunk = level.getChunk(
                        SectionPos.blockToSectionCoord(position.x),
                        SectionPos.blockToSectionCoord(position.z),
                        ChunkStatus.FULL,
                        false
                    )
                    if (chunk == null) continue
                    val cache: MultiBlockCasingCache = MultiBlockCasingCache.get(chunk) ?: continue
                    if (!cache.multiblockMap.containsKey(position)) continue
                    if (cache.multiblockMap.get(position)!! != controllerPos) continue
                    cache.multiblockMap.remove(position)
                    level.invalidateCapabilities(position)
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