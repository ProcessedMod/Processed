package redcrafter07.processed.gui.sync

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class SyncFieldList(val inner: List<Pair<String, ByteArray>>) {
    companion object {
        val STREAM_CODEC: StreamCodec<ByteBuf, SyncFieldList> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            Pair<String, ByteArray>::first,
            ByteBufCodecs.BYTE_ARRAY,
            Pair<String, ByteArray>::second,
            ::Pair
        ).apply(ByteBufCodecs.list()).map(::SyncFieldList, SyncFieldList::inner)
    }
}