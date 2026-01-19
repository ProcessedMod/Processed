package redcrafter07.processed

import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import redcrafter07.processed.rpc.CodecField
import redcrafter07.processed.rpc.CodecRegistry.getAny
import java.lang.reflect.Field
import java.util.HashMap
import kotlin.text.ifEmpty

object StreamCodecUtil {
    inline fun <reified T : Enum<T>> makeEnum(values: Collection<T>): StreamCodec<ByteBuf, T> {
        val byIdMap = ByIdMap.continuous({ it.ordinal }, values.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
        val codec = ByteBufCodecs.idMapper(byIdMap) { it.ordinal }
        return codec
    }

    fun <T> makeStreamCodec(clazz: Class<T>): StreamCodec<RegistryFriendlyByteBuf, T> {
        val fields = HashMap<String, Field>()
        for (field in clazz.fields) {
            val annotation = field.getAnnotation(CodecField::class.java) ?: continue
            val constructorName = annotation.constructorName.ifEmpty { field.name }
            fields[constructorName] = field
        }
        val order = mutableListOf<String>()
        for (constructor in clazz.constructors) {
            order.clear()
            var err = false
            for (parameter in constructor.parameters) {
                val field = fields[parameter.name]
                if (field == null) {
                    err = true
                    break
                }
                if (!parameter.type.isAssignableFrom(field.type)) {
                    err = true
                    break
                }
                order.add(parameter.name)
            }
            if (err) continue

            val codecs = lazy {
                order.map {
                    val field = fields[it]!!
                    val codec = getAny(field.type)
                        ?: throw IllegalStateException("Class ${clazz.name}'s field ${field.name} cannot be serialized")
                    Pair(codec, field)
                }
            }
            @Suppress("WRONG_NULLABILITY_FOR_JAVA_OVERRIDE") return object : StreamCodec<RegistryFriendlyByteBuf, T> {
                override fun decode(buf: RegistryFriendlyByteBuf): T {
                    val args = codecs.value.map { it.first.decode(buf) }.toTypedArray()
                    @Suppress("UNCHECKED_CAST") return constructor.newInstance(*args) as T
                }

                override fun encode(buf: RegistryFriendlyByteBuf, v: T) {
                    codecs.value.forEach {
                        it.first.encode(buf, it.second.get(v))
                    }
                }
            }
        }

        throw IllegalStateException("No constructor matches the @CodecField description of the type.")
    }
}