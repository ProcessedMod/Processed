package redcrafter07.processed.block.machine_abstractions

import io.netty.buffer.ByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.neoforged.neoforge.common.TranslatableEnum
import redcrafter07.processed.Translations
import java.util.function.IntFunction

enum class IoState(val id: Int, val stateName: String) : StringRepresentable, TranslatableEnum {
    None(0, "none"), Input(1, "input"), Output(2, "output"), InputOutput(3, "input_output"), Additional(
        4, "additional"
    ),
    Extra(5, "extra");

    companion object {
        val BY_ID: IntFunction<IoState> =
            ByIdMap.continuous(IoState::id, IoState.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
        val STREAM_CODEC: StreamCodec<ByteBuf, IoState> = ByteBufCodecs.idMapper(BY_ID, IoState::id)
    }

    override fun getSerializedName(): String = stateName
    override fun getTranslatedName(): Component = Translations.ioStateName(stateName)
    override fun toString(): String = stateName

    val next: IoState get() = BY_ID.apply(id + 1)
    val previous: IoState get() = BY_ID.apply(id - 1)
}