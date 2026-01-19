package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.rl
import redcrafter07.processed.rpc.MethodMeta
import redcrafter07.processed.rpc.RPCMethod

class MenuRPCPacket(val methodName: String, val containerId: Int, val data: ByteArray) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<MenuRPCPacket> = CustomPacketPayload.Type(rl("menu_rpc_packet"))
        val CODEC: StreamCodec<ByteBuf, MenuRPCPacket> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MenuRPCPacket::methodName,
            ByteBufCodecs.INT,
            MenuRPCPacket::containerId,
            ByteBufCodecs.BYTE_ARRAY,
            MenuRPCPacket::data,
            ::MenuRPCPacket
        )

        private val cache: MutableMap<Class<*>, Map<String, MethodMeta>> = HashMap()

        fun cacheFor(clazz: Class<*>): Map<String, MethodMeta> = cache.computeIfAbsent(clazz) {
            val methods = clazz.declaredMethods
            val map = HashMap<String, MethodMeta>()
            for (method in methods) {
                val annotation = method.getAnnotation(RPCMethod::class.java) ?: continue
                val methodName = annotation.name.ifEmpty { method.name }
                val meta = MethodMeta.of(method)
                map[methodName] = meta
            }
            map
        }
    }

    fun handle(context: IPayloadContext) {
        val openMenu = context.player().containerMenu ?: return
        if (openMenu.containerId != containerId) return

        val res = getMethod(openMenu, context.player() !is ServerPlayer && context.player().level().isClientSide) ?: return
        res.second.invoke(res.first, data, context)
    }

    fun getMethod(openMenu: Any, clientside: Boolean): Pair<Any, MethodMeta>? {
        for(clazz in ClassIter(openMenu.javaClass)) {
            val method = cacheFor(clazz)[methodName]
            if(method != null) return Pair(openMenu, method)
        }
        if(!clientside) return null

        val screen = Minecraft.getInstance().screen ?: return null
        for(clazz in ClassIter(screen.javaClass)) {
            val method = cacheFor(clazz)[methodName]
            if(method != null) return Pair(screen, method)
        }

        return null
    }

    override fun type() = TYPE

    class ClassIter(var clazz: Class<*>?) : Iterator<Class<*>> {
        override fun hasNext(): Boolean = clazz != null

        override fun next(): Class<*> {
            val clazz = clazz
            if(clazz != null) {
                this.clazz = clazz.superclass
                return clazz
            }
            throw IllegalStateException("both menuClass and screenClass are null")
        }

    }
}