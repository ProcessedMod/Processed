package redcrafter07.processed.rpc

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.core.RegistryAccess
import net.minecraft.network.Connection
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.connection.ConnectionType
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.network.RPCPacket

/**
 * NOTE: NEVER EVER EVER EVER EVER EVER EVER CALL ANY OF THE REGISTER METHODS OUTSIDE OF RpcFunctions.kt and NEVER call register. Please. Things *will* break
 */
@Suppress("unused")
object RpcRegistry {
    private val functions: MutableMap<String, Pair<Any, MethodMeta>> = HashMap()

    fun register(f: Function<*>, rl: String, args: Array<Class<*>>) {
        val fn = f.javaClass.methods.find { it.name == "invoke" }
        if (fn == null) throw IllegalStateException("tf???")
        functions[rl] = Pair(f, MethodMeta.of(fn, arrayOf(RPCSender::class.java,  *args)))
    }

    fun handle(name: String, data: ByteArray, context: IPayloadContext) {
        val fn = functions[name] ?: return
        fn.second.invoke(fn.first, data, context)
    }

    fun registerServer(rl: String, f: (RPCSender) -> Unit): ClientRpcMethod0 {
        register(f, rl, arrayOf())
        return ClientRpcMethod0(rl)
    }

    inline fun <reified T> registerServer(rl: String, noinline f: (RPCSender, T) -> Unit): ClientRpcMethod1<T> {
        register(f, rl, arrayOf(T::class.java))
        return ClientRpcMethod1(rl)
    }

    inline fun <reified T1, reified T2> registerServer(
        rl: String, noinline f: (RPCSender, T1, T2) -> Unit
    ): ClientRpcMethod2<T1, T2> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java))
        return ClientRpcMethod2(rl)
    }

    inline fun <reified T1, reified T2, reified T3> registerServer(
        rl: String, noinline f: (RPCSender, T1, T2, T3) -> Unit
    ): ClientRpcMethod3<T1, T2, T3> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java))
        return ClientRpcMethod3(rl)
    }

    inline fun <reified T1, reified T2, reified T3, reified T4> registerServer(
        rl: String, noinline f: (RPCSender, T1, T2, T3, T4) -> Unit
    ): ClientRpcMethod4<T1, T2, T3, T4> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java, T4::class.java))
        return ClientRpcMethod4(rl)
    }

    inline fun <reified T1, reified T2, reified T3, reified T4, reified T5> registerServer(
        rl: String, noinline f: (RPCSender, T1, T2, T3, T4, T5) -> Unit
    ): ClientRpcMethod5<T1, T2, T3, T4, T5> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java, T4::class.java, T5::class.java))
        return ClientRpcMethod5(rl)
    }

    fun registerClient(rl: String, f: (RPCSender) -> Unit): ServerRpcMethod0 {
        register(f, rl, arrayOf())
        return ServerRpcMethod0(rl)
    }

    inline fun <reified T> registerClient(rl: String, noinline f: (RPCSender, T) -> Unit): ServerRpcMethod1<T> {
        register(f, rl, arrayOf(T::class.java))
        return ServerRpcMethod1(rl)
    }

    inline fun <reified T1, reified T2> registerClient(
        rl: String, noinline f: (RPCSender, T1, T2) -> Unit
    ): ServerRpcMethod2<T1, T2> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java))
        return ServerRpcMethod2(rl)
    }

    inline fun <reified T1, reified T2, reified T3> registerClient(
        rl: String, noinline f: (RPCSender, T1, T2, T3) -> Unit
    ): ServerRpcMethod3<T1, T2, T3> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java))
        return ServerRpcMethod3(rl)
    }

    inline fun <reified T1, reified T2, reified T3, reified T4> registerClient(
        rl: String, noinline f: (RPCSender, T1, T2, T3, T4) -> Unit
    ): ServerRpcMethod4<T1, T2, T3, T4> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java, T4::class.java))
        return ServerRpcMethod4(rl)
    }

    inline fun <reified T1, reified T2, reified T3, reified T4, reified T5> registerClient(
        rl: String, noinline f: (RPCSender, T1, T2, T3, T4, T5) -> Unit
    ): ServerRpcMethod5<T1, T2, T3, T4, T5> {
        register(f, rl, arrayOf(T1::class.java, T2::class.java, T3::class.java, T4::class.java, T5::class.java))
        return ServerRpcMethod5(rl)
    }


    class ClientRpcMethod0(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer() = sendToServerInternal()
    }

    class ClientRpcMethod1<T>(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T) = sendToServerInternal(arg0 as Any)
    }

    class ClientRpcMethod2<T1, T2>(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2) = sendToServerInternal(arg0 as Any, arg1 as Any)
    }

    class ClientRpcMethod3<T1, T2, T3>(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3) = sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any)
    }

    class ClientRpcMethod4<T1, T2, T3, T4>(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3, arg3: T4) =
            sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any)
    }

    class ClientRpcMethod5<T1, T2, T3, T4, T5>(rl: String) : ClientRpcMethod(rl) {
        fun sendToServer(arg0: T1, arg1: T2, arg2: T3, arg3: T4, arg4: T5) =
            sendToServerInternal(arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any, arg4 as Any)
    }

    class ServerRpcMethod0(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer) = sendToClientInternal(player)
    }

    class ServerRpcMethod1<T>(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T) = sendToClientInternal(player, arg0 as Any)
    }

    class ServerRpcMethod2<T1, T2>(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any)
    }

    class ServerRpcMethod3<T1, T2, T3>(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any)
    }

    class ServerRpcMethod4<T1, T2, T3, T4>(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3, arg3: T4) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any)
    }

    class ServerRpcMethod5<T1, T2, T3, T4, T5>(rl: String) : ServerRpcMethod(rl) {
        fun sendToClient(player: ServerPlayer, arg0: T1, arg1: T2, arg2: T3, arg3: T4, arg4: T5) =
            sendToClientInternal(player, arg0 as Any, arg1 as Any, arg2 as Any, arg3 as Any, arg4 as Any)
    }


    open class ServerRpcMethod(val rl: String) {
        protected fun sendToClientInternal(player: ServerPlayer, vararg args: Any) {
            invoke(player.connection.connection, player.level().registryAccess(), rl, false, *args)
        }
    }

    open class ClientRpcMethod(val rl: String) {
        protected fun sendToServerInternal(vararg args: Any) {
            val player = Minecraft.getInstance().player ?: return
            invoke(player.connection.connection, player.level().registryAccess(), rl, true, *args)
        }
    }

    private fun invoke(
        connection: Connection, registryAccess: RegistryAccess, rl: String, clientside: Boolean, vararg args: Any
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