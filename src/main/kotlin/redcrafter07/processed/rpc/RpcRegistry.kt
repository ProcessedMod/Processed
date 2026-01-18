package redcrafter07.processed.rpc

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.core.RegistryAccess
import net.minecraft.network.Connection
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.connection.ConnectionType
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.network.RPCPacket

object RpcRegistry {
    private val functions: MutableMap<ResourceLocation, Pair<Any, MethodMeta>> = HashMap()

    private fun register(f: Any, rl: ResourceLocation) {
        val fn = f.javaClass.methods.find { it.name == "invoke" }
        if (fn == null) throw IllegalStateException("tf???")
        functions[rl] = Pair(f, MethodMeta.of(fn))
    }

    fun handle(name: ResourceLocation, data: ByteArray, context: IPayloadContext) {
        val fn = functions[name] ?: return
        fn.second.invoke(fn.first, data, context)
    }

    fun registerServer(rl: ResourceLocation, f: (RPCSender) -> Unit): ClientRpcMethod0 {
        register(f, rl)
        return ClientRpcMethod0(rl)
    }

    fun <T> registerServer(rl: ResourceLocation, f: (RPCSender, T) -> Unit): ClientRpcMethod1<T> {
        register(f, rl)
        return ClientRpcMethod1(rl)
    }

    fun <T1, T2> registerServer(rl: ResourceLocation, f: (RPCSender, T1, T2) -> Unit): ClientRpcMethod2<T1, T2> {
        register(f, rl)
        return ClientRpcMethod2(rl)
    }

    fun <T1, T2, T3> registerServer(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3) -> Unit
    ): ClientRpcMethod3<T1, T2, T3> {
        register(f, rl)
        return ClientRpcMethod3(rl)
    }

    fun <T1, T2, T3, T4> registerServer(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3, T4) -> Unit
    ): ClientRpcMethod4<T1, T2, T3, T4> {
        register(f, rl)
        return ClientRpcMethod4(rl)
    }

    fun <T1, T2, T3, T4, T5> registerServer(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3, T4, T5) -> Unit
    ): ClientRpcMethod5<T1, T2, T3, T4, T5> {
        register(f, rl)
        return ClientRpcMethod5(rl)
    }

    fun registerClient(rl: ResourceLocation, f: (RPCSender) -> Unit): ServerRpcMethod0 {
        register(f, rl)
        return ServerRpcMethod0(rl)
    }

    fun <T> registerClient(rl: ResourceLocation, f: (RPCSender, T) -> Unit): ServerRpcMethod1<T> {
        register(f, rl)
        return ServerRpcMethod1(rl)
    }

    fun <T1, T2> registerClient(rl: ResourceLocation, f: (RPCSender, T1, T2) -> Unit): ServerRpcMethod2<T1, T2> {
        register(f, rl)
        return ServerRpcMethod2(rl)
    }

    fun <T1, T2, T3> registerClient(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3) -> Unit
    ): ServerRpcMethod3<T1, T2, T3> {
        register(f, rl)
        return ServerRpcMethod3(rl)
    }

    fun <T1, T2, T3, T4> registerClient(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3, T4) -> Unit
    ): ServerRpcMethod4<T1, T2, T3, T4> {
        register(f, rl)
        return ServerRpcMethod4(rl)
    }

    fun <T1, T2, T3, T4, T5> registerClient(
        rl: ResourceLocation, f: (RPCSender, T1, T2, T3, T4, T5) -> Unit
    ): ServerRpcMethod5<T1, T2, T3, T4, T5> {
        register(f, rl)
        return ServerRpcMethod5(rl)
    }


    class ClientRpcMethod0(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer() = sendToServerInternal()
    }

    class ClientRpcMethod1<T>(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T) = sendToServerInternal(arg0 as Any)
    }

    class ClientRpcMethod2<T1, T2>(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2) = sendToServerInternal(arg0 as Any, arg1 as Any)
    }

    class ClientRpcMethod3<T1, T2, T3>(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3) = sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any)
    }

    class ClientRpcMethod4<T1, T2, T3, T4>(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3, arg3: T4) =
            sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any)
    }

    class ClientRpcMethod5<T1, T2, T3, T4, T5>(rl: ResourceLocation) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3, arg3: T4, arg4: T5) =
            sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any, arg4 as Any)
    }

    class ServerRpcMethod0(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer) = sendToClientInternal(player)
    }

    class ServerRpcMethod1<T>(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T) = sendToClientInternal(player, arg0 as Any)
    }

    class ServerRpcMethod2<T1, T2>(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any)
    }

    class ServerRpcMethod3<T1, T2, T3>(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any)
    }

    class ServerRpcMethod4<T1, T2, T3, T4>(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3, arg3: T4) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any)
    }

    class ServerRpcMethod5<T1, T2, T3, T4, T5>(rl: ResourceLocation) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3, arg3: T4, arg4: T5) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any, arg4 as Any)
    }


    open class ServerRpcMethod(val rl: ResourceLocation) {
        protected fun sendToClientInternal(player: ServerPlayer, vararg args: Any) {
            invoke(player.connection.connection, player.level().registryAccess(), rl, false, *args)
        }
    }

    open class ClientRpcMethod(val rl: ResourceLocation) {
        protected fun sendToServerInternal(vararg args: Any) {
            val player = Minecraft.getInstance().player ?: return
            invoke(player.connection.connection, player.level().registryAccess(), rl, true, *args)
        }
    }

    private fun invoke(
        connection: Connection,
        registryAccess: RegistryAccess,
        rl: ResourceLocation,
        clientside: Boolean,
        vararg args: Any
    ) {
        val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        for (arg in args) CodecRegistry.get(arg.javaClass)?.encode(buf, arg)
            ?: throw RuntimeException("Cannot encode ${arg.javaClass} for rpc")

        val customPacket = RPCPacket(rl, MenuRPCSender.toBytes(buf))
        val packet = if (clientside) ServerboundCustomPayloadPacket(customPacket) else ClientboundCustomPayloadPacket(
            customPacket
        )
        connection.send(packet)
    }
}