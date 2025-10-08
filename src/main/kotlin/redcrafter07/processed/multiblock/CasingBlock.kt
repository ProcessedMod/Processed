package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.status.ChunkStatus
import redcrafter07.processed.network.MultiblockDestroyPacket

class CasingBlock(properties: Properties) : Block(properties) {
    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if ((neighborState.block !is MultiblockBlock) && (neighborState.block !is CasingBlock) && (pos != neighborPos)) {
            val machineBlock: BlockPos? = MultiBlockCasingCache.getControllerForCasing(level, pos)

            if (machineBlock != null) {
                val chunkX = SectionPos.blockToSectionCoord(machineBlock.x)
                val chunkZ = SectionPos.blockToSectionCoord(machineBlock.z)
                if (level.hasChunk(chunkX, chunkZ)) {
                    val multiblock = level.getBlockEntity(machineBlock)
                    if (multiblock is MultiblockBlockEntity) multiblock.recheck()
                }
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        val chunk = level.getChunk(
            SectionPos.blockToSectionCoord(pos.x),
            SectionPos.blockToSectionCoord(pos.z),
            ChunkStatus.FULL,
            false
        ) ?: return super.onRemove(state, level, pos, newState, movedByPiston)
        val attachment = MultiBlockCasingCache.get(chunk)

        if (attachment != null && attachment.multiblockMap.containsKey(pos)) {
            val machineBlock =
                attachment.multiblockMap.get(pos) ?: return super.onRemove(state, level, pos, newState, movedByPiston)

            val chunkX = SectionPos.blockToSectionCoord(machineBlock.x)
            val chunkZ = SectionPos.blockToSectionCoord(machineBlock.z)
            if (level.hasChunk(chunkX, chunkZ)) {
                val multiblock = level.getBlockEntity(machineBlock)
                if (multiblock is MultiblockBlockEntity) multiblock.recheck()
            }

            if (attachment.multiblockMap.containsKey(pos)) {
                attachment.multiblockMap.remove(pos)
                attachment.set(chunk)

                if (level is ServerLevel && !level.isClientSide()) {
                    val packet = MultiblockDestroyPacket(listOf(pos), machineBlock)
                    for (player in level.players()) player.connection.send(packet)
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}
