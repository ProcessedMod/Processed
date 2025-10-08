package redcrafter07.processed.items

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.neoforged.neoforge.common.TranslatableEnum
import redcrafter07.processed.Translations
import java.util.function.IntFunction

enum class WrenchMode(val id: Int, val modeName: String) : StringRepresentable, TranslatableEnum {
    Config(0, "config"), Rotate(1, "rotate");

    companion object {
        val BY_ID: IntFunction<WrenchMode> =
            ByIdMap.continuous(WrenchMode::id, WrenchMode.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
        val CODEC: Codec<WrenchMode> = StringRepresentable.fromValues(WrenchMode::values)
        val STREAM_CODEC: StreamCodec<ByteBuf, WrenchMode> = ByteBufCodecs.idMapper(BY_ID, WrenchMode::id)
    }

    val next: WrenchMode get() = BY_ID.apply(id + 1)
    val previous: WrenchMode get() = BY_ID.apply(id - 1)

    override fun getSerializedName(): String = modeName
    override fun getTranslatedName(): Component = Translations.wrenchMode(modeName)
}