package redcrafter07.processed.rpc

import net.minecraft.client.Minecraft
import net.minecraft.core.RegistryAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import redcrafter07.processed.network.MenuRPCPacket

class MenuRPCSender private constructor(
    private val containerId: Int, private val registryAccess: RegistryAccess, val player: Player
) {
    companion object {
        fun of(menu: AbstractContainerMenu, player: Player) =
            MenuRPCSender(menu.containerId, player.level().registryAccess(), player)
    }

    fun sendToClient(method: String, vararg args: Any) {
        if (player !is ServerPlayer) throw RuntimeException("Cannot call rpcToClient from client-side")

        val packet = MenuRPCPacket(method, containerId, RpcRegistry.encode(registryAccess, args))
        player.connection.send(packet)
    }

    fun sendToServer(method: String, vararg args: Any) {
        if (player is ServerPlayer) throw RuntimeException("Cannot call rpcToServer from server-side")

        val packet = MenuRPCPacket(method, containerId, RpcRegistry.encode(registryAccess, args))
        Minecraft.getInstance().connection?.send(packet)
    }

    fun send(method: String, vararg args: Any) {
        val packet = MenuRPCPacket(method, containerId, RpcRegistry.encode(registryAccess, args))

        if (player is ServerPlayer) player.connection.send(packet)
        else Minecraft.getInstance().connection?.send(packet)
    }
}