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
            val methods = clazz.methods
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

        var obj: Any = openMenu
        var methods = cacheFor(openMenu.javaClass)
        var method = methods[methodName]
        // check the current screens methods if on client.
        if (method == null && context.player() !is ServerPlayer && context.player().level().isClientSide) {
            val screen = Minecraft.getInstance().screen
            if (screen != null) {
                obj = screen
                methods = cacheFor(screen.javaClass)
                method = methods[methodName]
            }
        }
        if (method == null) return
        method.invoke(obj, data, context)
    }

    override fun type() = TYPE
}