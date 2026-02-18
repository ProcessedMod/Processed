package redcrafter07.processed.covers

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation

class ClientCoverAttachment(val map: MutableMap<Int, Cover>) {
    companion object {
        private val MAP_CODEC = ByteBufCodecs.map<ByteBuf, Int, ResourceLocation, Map<Int, ResourceLocation>>(
            HashMap<Int, ResourceLocation>::newHashMap, ByteBufCodecs.INT, ResourceLocation.STREAM_CODEC
        )
        val CODEC = object : StreamCodec<ByteBuf, ClientCoverAttachment> {
            override fun decode(buf: ByteBuf): ClientCoverAttachment {
                val newMap = HashMap<Int, Cover>()
                for ((k, v) in MAP_CODEC.decode(buf)) newMap[k] = (Cover.REGISTRY.get(v) ?: continue)
                return ClientCoverAttachment(newMap)
            }

            override fun encode(buf: ByteBuf, attachment: ClientCoverAttachment) {
                MAP_CODEC.encode(buf, attachment.map.mapValues { (ignored, v) -> v.location.value })
            }

        }
    }
}