package redcrafter07.processed.multiblock

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.SectionPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.getFacingDirection
import redcrafter07.processed.loadBlockPositions
import redcrafter07.processed.network.MultiblockDestroyPacket
import redcrafter07.processed.saveBlockPositions

abstract class MultiblockBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    ProcessedMachine(type, pos, blockState) {
    protected abstract fun validator(): MultiblockValidator

    private var timeUntilNextCheck = 40
    var isAssembled: Boolean = false
        private set
    private var blocks: Set<BlockPos>? = null
    private var blocksOld: Set<BlockPos>? = null

    private fun synchroniseWithCache() {
        if (blocksOld == null) return
        val level = getLevel() ?: return

        for (block in blocksOld) {
            if (blocks != null && blocks!!.contains(block)) continue
            val chunk = level.getChunk(
                SectionPos.blockToSectionCoord(block.x),
                SectionPos.blockToSectionCoord(block.z),
                ChunkStatus.FULL,
                false
            )
            if (chunk == null) return
            val cache = MultiBlockCasingCache.get(chunk) ?: continue
            if (!cache.multiblockMap.containsKey(block)) continue
            if (cache.multiblockMap.get(block) != blockPos) continue
            cache.multiblockMap.remove(block)
            cache.set(chunk)
        }

        if (blocks == null) {
            blocksOld = null
            return
        }
        for (block in blocks) {
            if (blocksOld!!.contains(block)) continue
            val chunk = level.getChunk(
                SectionPos.blockToSectionCoord(block.x),
                SectionPos.blockToSectionCoord(block.z),
                ChunkStatus.FULL,
                false
            )
            if (chunk == null) return
            val cache = MultiBlockCasingCache.getOrDefault(chunk)
            cache.multiblockMap[block] = blockPos
            cache.set(chunk)
        }
        blocksOld = null
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        isAssembled = tag.getBoolean("isAssembled")
        if (tag.contains("blocks", 11)) {
            blocksOld = blocks ?: mutableSetOf()
            blocks = setOf(*loadBlockPositions(tag.getIntArray("blocks")).toTypedArray())
        } else {
            blocksOld = blocks ?: mutableSetOf()
            blocks = null
            isAssembled = false
        }
    }

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        tag.putBoolean("isAssembled", isAssembled)
        if (blocks != null) tag.put("blocks", saveBlockPositions(blocks!!.stream().toList()))
    }

    fun onRemove(level: Level) {
        if (blocks == null) return
        if (level is ServerLevel && !level.isClientSide()) {
            val packet = MultiblockDestroyPacket(blocks!!.stream().toList(), blockPos)
            for (player in level.players()) player.connection.send(packet)
        }
        for (pos in blocks) {
            MultiBlockCasingCache.removeCasing(level, pos)
            level.invalidateCapabilities(pos)
        }
    }

    fun tileTickCommon(level: Level, pos: BlockPos, state: BlockState) {
    }

    fun tileTickServer(level: ServerLevel, pos: BlockPos, state: BlockState) {
    }

    fun tileTickClient(level: ClientLevel, pos: BlockPos, state: BlockState) {
    }

    override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
    }

    override fun clientTick(level: ClientLevel, pos: BlockPos, state: BlockState) {
    }

    override fun commonTick(level: Level, pos: BlockPos, state: BlockState) {
        if (!isAssembled || !isRunning) return
        tileTickCommon(level, pos, state)
        if (level is ServerLevel && !level.isClientSide()) tileTickServer(level, pos, state)
        else if (level is ClientLevel && level.isClientSide()) tileTickClient(level, pos, state)
    }

    override fun tickNoProcessing(level: Level, pos: BlockPos, state: BlockState) {
        synchroniseWithCache()

        if (level is ServerLevel && !level.isClientSide()) {
            if (isAssembled) return
            if (timeUntilNextCheck > 0) {
                timeUntilNextCheck -= 1
                return
            }
            timeUntilNextCheck = 40

            recheck()
        }
    }

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState): IItemHandler? {
        if (isAssembled) return super.itemCapabilityForSide(side, state)
        return null
    }

    private val levelServer: ServerLevel?
        get() {
            val level = level
            if (level is ServerLevel && !level.isClientSide) return level
            return null
        }

    fun recheck() {
        val serverLevel = this.levelServer ?: return

        var affectedBlocks =
            validator().getBlocks(serverLevel, blockPos, getFacingDirection(blockState))
        if (affectedBlocks != null) {
            for (block in affectedBlocks) {
                val controllerPos: BlockPos? = MultiBlockCasingCache.getControllerForCasing(serverLevel, block)
                if (controllerPos != null && controllerPos !== blockPos) {
                    affectedBlocks = null
                    break
                }
            }
        }

        val old = blocks ?: setOf()
        blocks = null
        val wasPreviouslyAssembled = isAssembled
        isAssembled = affectedBlocks != null && !affectedBlocks.isEmpty()
        if (affectedBlocks == null || affectedBlocks.isEmpty()) {
            for (pos in old) {
                MultiBlockCasingCache.removeCasing(serverLevel, pos)
                serverLevel.invalidateCapabilities(pos)
            }
            invalidateCapabilities()
            if (!old.isEmpty()) sync()
            return
        }
        for (pos in old) {
            if (pos == blockPos) continue
            if (affectedBlocks.contains(pos)) continue
            MultiBlockCasingCache.removeCasing(serverLevel, pos)
            serverLevel.invalidateCapabilities(pos)
        }

        for (pos in affectedBlocks) {
            if (pos == blockPos) continue

            MultiBlockCasingCache.setControllerForCasing(serverLevel, pos, blockPos)
            serverLevel.invalidateCapabilities(pos)
        }
        invalidateCapabilities()
        blocks = affectedBlocks
        sync()

        var min: MutableBlockPos? = null
        var max: MutableBlockPos? = null

        for (pos in affectedBlocks) {
            if (min == null) min = MutableBlockPos(pos.x, pos.y, pos.z)
            else {
                if (pos.x < min.x) min.x = pos.x
                if (pos.y < min.y) min.y = pos.y
                if (pos.z < min.z) min.z = pos.z
            }
            if (max == null) max = MutableBlockPos(pos.x, pos.y, pos.z)
            else {
                if (pos.x > max.x) max.x = pos.x
                if (pos.y > max.y) max.y = pos.y
                if (pos.z > max.z) max.z = pos.z
            }
        }
        if (min == null || max == null) return
        max.x = max.x + 1
        max.y = max.y + 1
        max.z = max.z + 1

        min.x = min.x - 1
        min.y = min.y - 1
        min.z = min.z - 1

        if (wasPreviouslyAssembled) return

        // redstone particles
        // front + back
        for (y in min.y..max.y) {
            for (z in min.z..max.z) {
                // front
                spawnAssembledParticle(min.x, y, z, serverLevel)
                // back
                spawnAssembledParticle(max.x, y, z, serverLevel)
            }
        }

        // bottom + top
        for (x in min.x..max.x) {
            for (z in min.z..max.z) {
                // bottom
                spawnAssembledParticle(x, min.y, z, serverLevel)
                // top
                spawnAssembledParticle(x, max.y, z, serverLevel)
            }
        }

        // left + right
        for (y in min.y..max.y) {
            for (x in min.x..max.x) {
                // left
                spawnAssembledParticle(x, y, min.z, serverLevel)
                // right
                spawnAssembledParticle(x, y, max.z, serverLevel)
            }
        }
    }

    private fun spawnAssembledParticle(x: Int, y: Int, z: Int, level: ServerLevel) {
        level.sendParticles(ParticleTypes.END_ROD, x + .5, y + .5, z + .5, 1, 0.0, 0.0, 0.0, 0.0)
    }
}