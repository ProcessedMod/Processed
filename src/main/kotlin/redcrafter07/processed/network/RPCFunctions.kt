package redcrafter07.processed.network

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.RegisterEvent
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.items.*
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import redcrafter07.processed.rpc.RpcRegistry

// RPC Functions. Yay :D
// Note: If you  add a new one, ensure that none of the arguments have generic parameters. If they do, add a class wrapping that type
// (e.g. List<BlockPos> -> BlockPosList and add class BlockPosList(List<BlockPos>)).
// After that, ensure codecs for all those types are registered in CodecRegistry.kt
object RPCFunctions {
    val selectPlanetoid =
        RpcRegistry.registerServer("select_planetoid") { sender, planetoid: ResourceLocation, translatedName: String, hand: InteractionHand ->
            val item = sender.player.getItemInHand(hand)
            if (item.`is`(ModItems.LOCATION_SELECTOR)) item.set(
                ModDataComponents.BOUND_PLANETOID, LocationSelectorItem.BoundPlanetoid(planetoid, translatedName)
            )
        }
    val changeProcessedBlockIO =
        RpcRegistry.registerServer<BlockPos, IoState, BlockSide, Boolean>("change_io") { sender, block, state, side, itemOrFluid ->
            val level = sender.player.level()
            val blockEntity = level.getBlockEntity(block)
            if (blockEntity is ProcessedMachine) {
                blockEntity.setSide(itemOrFluid, side, state)
                blockEntity.invalidateCapabilities()
            }
        }

    val runLaunchControllerAnimation =
        RpcRegistry.registerClient<BlockPos, Boolean>("start_launch_controller_animation") { sender, controllerPos, launch ->
            val level = sender.player.level()
            if (level.isClientSide && level is ClientLevel) {
                val be = level.getBlockEntity(controllerPos)
                if (be is LaunchControllerBlockEntity) {
                    if (launch) be.animator.startLaunchAnimation(
                        level, controllerPos, level.getBlockState(controllerPos)
                    )
                    else be.animator.startLandingAnimation(level, controllerPos, level.getBlockState(controllerPos))
                }
            }
        }

    val updateWrenchMode = RpcRegistry.registerServer<WrenchMode>("update_wrench_mode") { sender, mode ->
        val item = sender.player.mainHandItem
        if (item.item is WrenchItem) WrenchItem.setMode(item, mode)
    }

    private val multiblockDestroyNotification =
        RpcRegistry.registerClient<LongArray, BlockPos>("multiblock_destroy_notification") { sender, relPackedPositions, controllerPos ->
            sender.player.level().use { level ->
                val x = controllerPos.x
                val y = controllerPos.y
                val z = controllerPos.z
                for (packedPos in relPackedPositions) {
                    val relPos = BlockPos.of(packedPos)
                    val pos = BlockPos(x + relPos.x, y + relPos.y, z + relPos.z)
                    val chunk = level.getChunk(
                        SectionPos.blockToSectionCoord(pos.x),
                        SectionPos.blockToSectionCoord(pos.z),
                        ChunkStatus.FULL,
                        false
                    )
                    if (chunk == null) continue
                    val cache: MultiBlockBlockCache = MultiBlockBlockCache.get(chunk) ?: continue
                    if (!cache.contains(pos)) continue
                    if (cache.getController(pos) != controllerPos) continue
                    cache.removeBlock(pos)
                    level.invalidateCapabilities(pos)
                    cache.set(chunk)
                }
            }
        }
    fun notifyMultiblockDestroyed(player: ServerPlayer, relPackedPositions: LongArray, controllerPosition: BlockPos) {
        multiblockDestroyNotification.sendToClient(player, relPackedPositions, controllerPosition)
    }

    fun register(bus: IEventBus) {
        // no-op that runs at init. Should probably do smth like DeferredRegistry but idc
        bus.addListener<RegisterEvent> {}
    }
}