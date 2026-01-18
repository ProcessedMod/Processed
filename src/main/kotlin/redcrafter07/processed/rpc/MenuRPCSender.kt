package redcrafter07.processed.rpc

import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.neoforged.neoforge.network.connection.ConnectionType
import redcrafter07.processed.network.MenuRPCPacket

class MenuRPCSender private constructor(
    private val containerId: Int, private val registryAccess: RegistryAccess, private val player: Player
) {
    companion object {
        fun of(menu: AbstractContainerMenu, player: Player) =
            MenuRPCSender(menu.containerId, player.level().registryAccess(), player)

        fun toBytes(buf: RegistryFriendlyByteBuf): ByteArray {
            buf.readerIndex(0)
            val want = buf.readableBytes()
            var arr = buf.array()
            if(arr.size == want) return arr
            arr = ByteArray(want)
            buf.readBytes(arr)
            return arr
        }
    }

    fun rpcToClient(method: String, vararg args: Any) {
        val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        for (arg in args) CodecRegistry.get(arg.javaClass)?.encode(buf, arg)
            ?: throw RuntimeException("Cannot encode ${arg.javaClass} for rpc")
        if (player is ServerPlayer) player.connection.send(
            MenuRPCPacket(
                method, containerId, toBytes(buf)
            )
        ) else throw RuntimeException("Cannot call rpcToClient from client-side")
    }

    fun rpcToServer(method: String, vararg args: Any) {
        val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        for (arg in args) CodecRegistry.get(arg.javaClass)?.encode(buf, arg)
            ?: throw RuntimeException("Cannot encode ${arg.javaClass} for rpc")
        if (player is ServerPlayer || !player.level().isClientSide) throw RuntimeException("Cannot call rpcToServer from server-side")
        else Minecraft.getInstance().connection?.send(
            MenuRPCPacket(
                method, containerId, toBytes(buf)
            )
        )
    }
}