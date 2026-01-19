package redcrafter07.processed.rpc

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.gui.widgets.FluidWidget
import redcrafter07.processed.items.WrenchMode
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.miner.MinerCalc

object CodecRegistry {
    private val codecs: MutableMap<Class<*>, StreamCodec<RegistryFriendlyByteBuf, *>> = HashMap()

    init {
        register(Boolean::class.java, ByteBufCodecs.BOOL)
        register(
            java.lang.Boolean::class.java,
            ByteBufCodecs.BOOL.map({ it as java.lang.Boolean }, java.lang.Boolean::booleanValue)
        )

        register(Byte::class.java, ByteBufCodecs.BYTE)
        register(
            java.lang.Byte::class.java, ByteBufCodecs.BYTE.map({ it as java.lang.Byte }, java.lang.Byte::toByte)
        )

        register(Short::class.java, ByteBufCodecs.SHORT)
        register(
            java.lang.Short::class.java, ByteBufCodecs.SHORT.map({ it as java.lang.Short }, java.lang.Short::toShort)
        )

        register(Int::class.java, ByteBufCodecs.INT)
        register(Integer::class.java, ByteBufCodecs.INT.map({ it as Integer }, Integer::toInt))

        register(Long::class.java, ByteBufCodecs.VAR_LONG)
        register(
            java.lang.Long::class.java, ByteBufCodecs.VAR_LONG.map({ it as java.lang.Long }, java.lang.Long::toLong)
        )

        register(Float::class.java, ByteBufCodecs.FLOAT)
        register(
            java.lang.Float::class.java, ByteBufCodecs.FLOAT.map({ it as java.lang.Float }, java.lang.Float::toFloat)
        )

        register(Double::class.java, ByteBufCodecs.DOUBLE)
        register(
            java.lang.Double::class.java,
            ByteBufCodecs.DOUBLE.map({ it as java.lang.Double }, java.lang.Double::toDouble)
        )

        register(String::class.java, ByteBufCodecs.STRING_UTF8)
        register(ByteArray::class.java, ByteBufCodecs.BYTE_ARRAY)
        register(Tag::class.java, ByteBufCodecs.TAG)
        register(CompoundTag::class.java, ByteBufCodecs.COMPOUND_TAG)
        register(ResourceLocation::class.java, ResourceLocation.STREAM_CODEC)
        register(
            InteractionHand::class.java,
            ByteBufCodecs.BOOL.map(
                { if (it) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND },
                { it == InteractionHand.MAIN_HAND })
        )
        register(FluidWidget.InsertionKind::class.java, FluidWidget.InsertionKind.STREAM_CODEC)
        register(BlockPos::class.java, BlockPos.STREAM_CODEC)
        register(IoState::class.java, IoState.STREAM_CODEC)
        register(BlockSide::class.java, BlockSide.STREAM_CODEC)
        register(MinerCalc.Result::class.java, MinerCalc.Result.STREAM_CODEC)
        register(LevelMinerData.LaunchedMinerData::class.java, LevelMinerData.LaunchedMinerData.STREAM_CODEC)
        register(WrenchMode::class.java, WrenchMode.STREAM_CODEC)
        register(
            LongArray::class.java, ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()).map<LongArray>(
                MutableList<Long>::toLongArray, LongArray::toList
            )
        )
    }

    private fun <T> register(clazz: Class<T>, codec: StreamCodec<ByteBuf, T>) {
        if (codecs.containsKey(clazz)) throw IllegalStateException("A codec for $clazz is already registered")
        codecs[clazz] = codec.cast()
    }

    private fun <T> registerRegistryFriendly(clazz: Class<T>, codec: StreamCodec<RegistryFriendlyByteBuf, T>) {
        if (codecs.containsKey(clazz)) throw IllegalStateException("A codec for $clazz is already registered")
        codecs[clazz] = codec
    }

    private fun find(clazz: Class<*>?): StreamCodec<RegistryFriendlyByteBuf, *>? {
        var clazz = clazz
        while (clazz != null) {
            val c = codecs[clazz]
            if (c != null) return c
            clazz = clazz.superclass
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>): StreamCodec<RegistryFriendlyByteBuf, T>? =
        find(clazz) as StreamCodec<RegistryFriendlyByteBuf, T>?

    @Suppress("UNCHECKED_CAST")
    fun getAny(clazz: Class<*>): StreamCodec<RegistryFriendlyByteBuf, Any>? =
        find(clazz) as StreamCodec<RegistryFriendlyByteBuf, Any>?
}