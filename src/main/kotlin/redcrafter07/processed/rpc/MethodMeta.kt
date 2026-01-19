package redcrafter07.processed.rpc

import io.netty.buffer.Unpooled
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.lang.reflect.Method

class MethodMeta(
    val codecs: Array<StreamCodec<RegistryFriendlyByteBuf, *>>, val firstArgSender: Boolean, val method: Method
) {
    companion object {
        fun of(method: Method): MethodMeta {
            if (method.parameters.size == 0) return MethodMeta(arrayOf(), false, method)
            else if (method.parameters.size == 1 && RPCSender::class.java.isAssignableFrom(method.parameters[0].type)) return MethodMeta(
                arrayOf(), true, method
            )

            val firstArgSender = RPCSender::class.java.isAssignableFrom(method.parameters[0].type)

            val start = if (firstArgSender) 1 else 0

            val codecs = method.parameters.slice(start..<method.parameterCount).map {
                CodecRegistry.get(it.type)
                    ?: throw RuntimeException("Could not determine a codec for parameter ${it.name} of type ${it.parameterizedType}")
            }.toTypedArray()

            return MethodMeta(codecs, firstArgSender, method)
        }

        fun of(method: Method, params: Array<Class<*>>): MethodMeta {
            assert(method.parameterCount == params.size)
            if (params.isEmpty()) return MethodMeta(arrayOf(), false, method)
            else if (params.size == 1 && RPCSender::class.java.isAssignableFrom(params[0])) return MethodMeta(
                arrayOf(), true, method
            )

            val firstArgSender = RPCSender::class.java.isAssignableFrom(params[0])

            val start = if (firstArgSender) 1 else 0

            val codecs = (start..<method.parameterCount).map {
                CodecRegistry.get(params[it])
                    ?: throw RuntimeException("Could not determine a codec for parameter ${method.parameters[it].name} of type ${method.parameters[it].parameterizedType}")
            }.toTypedArray()

            return MethodMeta(codecs, firstArgSender, method)
        }
    }

    fun invoke(obj: Any?, data: ByteArray, packetCtx: IPayloadContext) {
        if (codecs.isEmpty()) {
            if (firstArgSender) {
                method.invoke(obj, RPCSender.of(packetCtx))
                return
            } else {
                method.invoke(obj)
                return
            }
        }

        val args = arrayOfNulls<Any>(codecs.size)
        val buf = RegistryFriendlyByteBuf(
            Unpooled.wrappedBuffer(data),
            packetCtx.player().level().registryAccess(),
            packetCtx.listener().connectionType
        )
        if (firstArgSender) args[0] = RPCSender.of(packetCtx)
        for (i in 0..<codecs.size) args[i] = codecs[i].decode(buf)
        method.isAccessible = true
        try {
            if (firstArgSender) method.invoke(obj, RPCSender.of(packetCtx), *args)
            else method.invoke(obj, *args)
        } finally {
            method.isAccessible = false
        }
    }
}