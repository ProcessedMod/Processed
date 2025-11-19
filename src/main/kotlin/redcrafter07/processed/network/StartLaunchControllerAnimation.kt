package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.rl
import java.io.IOException

class StartLaunchControllerAnimation(val controllerPos: BlockPos, val launch: Boolean) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<StartLaunchControllerAnimation> =
            CustomPacketPayload.Type(rl("start_launch"))
        val CODEC: StreamCodec<ByteBuf, StartLaunchControllerAnimation> = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            StartLaunchControllerAnimation::controllerPos,
            ByteBufCodecs.BOOL,
            StartLaunchControllerAnimation::launch,
            ::StartLaunchControllerAnimation
        )
    }

    fun handleClient(context: IPayloadContext) {
        try {
            val level = context.player().level()
            if (!level.isClientSide || level !is ClientLevel) return
            val be = level.getBlockEntity(controllerPos)
            if (be is LaunchControllerBlockEntity) {
                if (launch) be.animator.startLaunchAnimation(level, controllerPos, level.getBlockState(controllerPos))
                else be.animator.startLandingAnimation(level, controllerPos, level.getBlockState(controllerPos))
            }
        } catch (_: IOException) {
        } catch (_: UnsupportedOperationException) {
        }
    }

    override fun type(): CustomPacketPayload.Type<StartLaunchControllerAnimation> = TYPE
}