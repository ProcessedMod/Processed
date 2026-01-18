package redcrafter07.processed.rpc

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.handling.IPayloadContext

sealed interface RPCSender {
    val fromClient: Boolean
    val serverPlayer: ServerPlayer?
    val player: Player
    val packetCtx: IPayloadContext

    class ClientRPCSender(override val serverPlayer: ServerPlayer, override val packetCtx: IPayloadContext) :
        RPCSender {
        override val fromClient = true
        override val player = serverPlayer
    }

    class ServerRPCSender(override val packetCtx: IPayloadContext) : RPCSender {
        override val serverPlayer = null
        override val fromClient = false
        override val player: Player = packetCtx.player()
    }

    companion object {
        fun of(packetCtx: IPayloadContext): RPCSender = packetCtx.player().run {
            if (level().isClientSide || this !is ServerPlayer) ServerRPCSender(packetCtx)
            else ClientRPCSender(this, packetCtx)
        }
    }
}