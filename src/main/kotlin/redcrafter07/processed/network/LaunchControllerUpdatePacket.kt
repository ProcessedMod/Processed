package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.miner.MinerCalc
import redcrafter07.processed.miner.Planetoid
import redcrafter07.processed.rl
import java.io.IOException

class LaunchControllerUpdatePacket(
    val block: BlockPos, planetoid: ResourceLocation?, calc: MinerCalc.Result?, data: LevelMinerData.LaunchedMinerData?
) : CustomPacketPayload {
    val planetoid = planetoid ?: RL
    val calc = calc ?: MinerCalc.Result(-10, -10.0, -10.0, -10.0)
    val data = data ?: LevelMinerData.LaunchedMinerData(RL, -10, 0, 0, 0, BlockPos.ZERO)

    companion object {
        val RL: ResourceLocation = ResourceLocation.fromNamespaceAndPath("", "")
        val TYPE: CustomPacketPayload.Type<LaunchControllerUpdatePacket> =
            CustomPacketPayload.Type(rl("launch_controller_update"))
        val CODEC: StreamCodec<ByteBuf, LaunchControllerUpdatePacket> = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            LaunchControllerUpdatePacket::block,
            ResourceLocation.STREAM_CODEC,
            LaunchControllerUpdatePacket::planetoid,
            MinerCalc.Result.STREAM_CODEC,
            LaunchControllerUpdatePacket::calc,
            LevelMinerData.LaunchedMinerData.STREAM_CODEC,
            LaunchControllerUpdatePacket::data,
            ::LaunchControllerUpdatePacket
        )
    }

    override fun type(): CustomPacketPayload.Type<LaunchControllerUpdatePacket> = TYPE

    fun handleClient(context: IPayloadContext) {
        try {
            val level = context.player().level()
            val blockEntity = level.getBlockEntity(block)
            if (blockEntity !is LaunchControllerBlockEntity) return
            blockEntity.clientLastResult = if (calc.totalTime < -1.0) null else calc
            blockEntity.clientLastDest = level.registryAccess().registry(Planetoid.REGISTRY_KEY).get().get(planetoid)
            blockEntity.clientLastLaunchedMinerData = if (data.itemAmount < 0) null else data
        } catch (_: IOException) {
        }
    }
}
