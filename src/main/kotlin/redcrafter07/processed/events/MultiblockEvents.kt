package redcrafter07.processed.events

import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.network.MultiblockDestroyPacket

@EventBusSubscriber(modid = ProcessedMod.ID)
object MultiblockEvents {
    @SubscribeEvent
    fun onBlockRemoved(e: BlockEvent.BreakEvent) {
        val level = e.level
        val pos = e.pos
        if (level.isClientSide) return
        val machineBlock = MultiBlockBlockCache.getController(level, pos) ?: return
        if (machineBlock == pos) return

        val chunkX = SectionPos.blockToSectionCoord(machineBlock.x)
        val chunkZ = SectionPos.blockToSectionCoord(machineBlock.z)
        if (level.hasChunk(chunkX, chunkZ)) {
            val multiblock = level.getBlockEntity(machineBlock)
            if (multiblock is MultiblockBlockEntity) multiblock.partBlockDestroyed(pos)
        } else {
            if (!MultiBlockBlockCache.removeBlock(level, pos)) return

            if (level is ServerLevel && !level.isClientSide()) {
                val packet = MultiblockDestroyPacket(listOf(pos.subtract(machineBlock).asLong()), machineBlock)
                for (player in level.players()) player.connection.send(packet)
            }

        }
    }

    @SubscribeEvent
    fun onBlockPlace(e: BlockEvent.EntityPlaceEvent) {
        if (e.blockSnapshot.state.`is`(e.blockSnapshot.currentState.block)) return
        val level = e.level
        val pos = e.pos
        if (level.isClientSide) return
        val machineBlock = MultiBlockBlockCache.getController(level, pos) ?: return
        if (machineBlock == pos) return

        val chunkX = SectionPos.blockToSectionCoord(machineBlock.x)
        val chunkZ = SectionPos.blockToSectionCoord(machineBlock.z)
        if (level.hasChunk(chunkX, chunkZ)) {
            val multiblock = level.getBlockEntity(machineBlock)
            if (multiblock is MultiblockBlockEntity) multiblock.partBlockDestroyed(pos)
        }
    }

    @SubscribeEvent
    fun onBlockPlaceMany(e: BlockEvent.EntityMultiPlaceEvent) {
        onBlockPlace(e)

        val level = e.level
        if (level.isClientSide) return
        for (snapshot in e.replacedBlockSnapshots) {
            if (snapshot.state.`is`(snapshot.currentState.block)) return
            val pos = snapshot.pos
            val machineBlock = MultiBlockBlockCache.getController(level, pos) ?: return
            if (machineBlock == pos) return

            val chunkX = SectionPos.blockToSectionCoord(machineBlock.x)
            val chunkZ = SectionPos.blockToSectionCoord(machineBlock.z)
            if (level.hasChunk(chunkX, chunkZ)) {
                val multiblock = level.getBlockEntity(machineBlock)
                if (multiblock is MultiblockBlockEntity) multiblock.partBlockDestroyed(pos)
            }
        }
    }
}