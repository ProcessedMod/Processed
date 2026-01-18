package redcrafter07.processed.rpc

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import java.lang.reflect.Field
import java.util.HashMap

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
    }

    fun <T> register(clazz: Class<T>, codec: StreamCodec<ByteBuf, T>) {
        if (codecs.containsKey(clazz)) throw IllegalStateException("A codec for $clazz is already registered")
        codecs[clazz] = codec.cast()
    }

    fun <T> registerRegistryFriendly(clazz: Class<T>, codec: StreamCodec<RegistryFriendlyByteBuf, T>) {
        if (codecs.containsKey(clazz)) throw IllegalStateException("A codec for $clazz is already registered")
        codecs[clazz] = codec
    }

    inline fun <reified T : Enum<T>> registerEnum(clazz: Class<T>, values: Collection<T>): StreamCodec<ByteBuf, T> {
        val byIdMap = ByIdMap.continuous({ it.ordinal }, values.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
        val codec = ByteBufCodecs.idMapper(byIdMap) { it.ordinal }
        register(clazz, codec)
        return codec
    }

    fun <T> makeAndRegisterStreamCodec(clazz: Class<T>): StreamCodec<RegistryFriendlyByteBuf, T> {
        val codec = makeStreamCodec(clazz)
        registerRegistryFriendly(clazz, codec)
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

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>): StreamCodec<RegistryFriendlyByteBuf, T>? =
        codecs[clazz] as StreamCodec<RegistryFriendlyByteBuf, T>?

    @Suppress("UNCHECKED_CAST")
    fun getAny(clazz: Class<*>): StreamCodec<RegistryFriendlyByteBuf, Any>? =
        codecs[clazz] as StreamCodec<RegistryFriendlyByteBuf, Any>?
}