package redcrafter07.processed.gui

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import redcrafter07.processed.block.LaunchControllerBlock
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu
import redcrafter07.processed.gui.sync.MenuSynced
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.miner.MinerCalc
import redcrafter07.processed.miner.Planetoid
import java.util.Optional

class LaunchControllerMenu(
    containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity?, val data: ContainerData
) : ProcessedContainerMenu(
    ModMenuTypes.LAUNCH_CONTROLLER_MENU.get(), containerId, playerInventory
) {
    @MenuSynced(condition = "noLaunched")
    var lastDest: Optional<ResourceLocation> = Optional.empty()
        get() {
            if (level.isClientSide) return field
            // won't be synced anyways cuz noLaunched (this fields condition) returns false
            else if (be.minerData != null) return Optional.empty()
            val v = be.lastDestination?.first?.let {
                level.registryAccess().registry(Planetoid.REGISTRY_KEY).get().getKey(it)
            }
            return Optional.ofNullable(v)
        }

    @MenuSynced(condition = "noLaunched")
    var lastResult: Optional<MinerCalc.Result> = Optional.empty()
        get() = if (level.isClientSide) field else Optional.ofNullable(be.lastResult)

    @MenuSynced
    var lastLaunched: Optional<LevelMinerData.LaunchedMinerData> = Optional.empty()
        get() = if (level.isClientSide || level !is ServerLevel) field
        else Optional.ofNullable(be.minerData?.let { LevelMinerData.get(level, it) })

    private fun lastDestCodec(): StreamCodec<ByteBuf, Optional<ResourceLocation>> =
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC)

    private fun lastResultCodec(): StreamCodec<ByteBuf, Optional<MinerCalc.Result>> =
        ByteBufCodecs.optional(MinerCalc.Result.STREAM_CODEC)

    private fun lastLaunchedCodec(): StreamCodec<ByteBuf, Optional<LevelMinerData.LaunchedMinerData>> =
        ByteBufCodecs.optional(LevelMinerData.LaunchedMinerData.STREAM_CODEC)

    private fun noLaunched(_v: Any) = be.minerData == null

    val be = requireFurnaceBlockEntity(blockEntity)

    companion object {
        fun requireFurnaceBlockEntity(b: BlockEntity?): LaunchControllerBlockEntity {
            if (b == null) throw IllegalStateException("no block entity found  :<")
            if (b !is LaunchControllerBlockEntity) throw IllegalStateException("non-powered furnace block entity found :<")
            return b
        }
    }

    init {
        checkContainerSize(playerInventory, 2)
    }

    val level: Level = playerInventory.player.level()

    init {
        addDataSlots(data)
    }

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), SimpleContainerData(4)
    )

    override fun customSlotCount(): Int = 0

    override fun stillValid(player: Player): Boolean =
        ContainerLevelAccess.create(level, be.blockPos).evaluate { level, pos ->
            if (level.getBlockState(pos).block is LaunchControllerBlock) return@evaluate player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
            ) <= 64.0
            return@evaluate false
        }.orElse(false)
}