package redcrafter07.processed.multiblock

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.getFacingDirection
import redcrafter07.processed.network.RPCFunctions

abstract class MultiblockBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    ProcessedMachine(type, pos, blockState) {
    protected abstract fun validator(): MultiblockValidator

    private var timeUntilNextCheck = 40

    // The parts of this multiblock that were destroyed last tick.
    private val partsDestroyed: ArrayList<BlockPos> = ArrayList()
    var isAssembled: Boolean = false
        private set
    private var blocks: Set<BlockPos>? = null
    protected var specialBlocks: Map<SpecialBlockType, List<BlockPos>> = mapOf()

    protected fun specialBlock(type: SpecialBlockType) =
        specialBlocks[type].run { if (this.isNullOrEmpty()) null else this[0] }

    open fun state(): Component? = null

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        isAssembled = tag.getBoolean("isAssembled")

        // blocks are stored relative to the controller as packed longs. This is fine because the MultiBlockCasingCache also assumes the controller is not too far.
        if (tag.contains("blocks", Tag.TAG_LONG_ARRAY.toInt())) {
            val blocks = mutableSetOf<BlockPos>()
            val x = blockPos.x
            val y = blockPos.y
            val z = blockPos.z
            val longs = tag.getLongArray("blocks")
            longs.map(BlockPos::of).map { pos -> BlockPos(pos.x + x, pos.y + y, pos.z + z) }.forEach(blocks::add)
            this.blocks = blocks

            if (tag.contains("specialBlocks", Tag.TAG_COMPOUND.toInt())) {
                val special = tag.getCompound("specialBlocks")
                val specialBlocks = hashMapOf<SpecialBlockType, List<BlockPos>>()
                for (specialBlock in SpecialBlockType.values) {
                    if (special.contains(specialBlock.key, Tag.TAG_INT.toInt())) {
                        val index = special.getInt(specialBlock.key)
                        val unpacked = BlockPos.of(longs[index])
                        specialBlocks[specialBlock] = listOf(BlockPos(unpacked.x + x, unpacked.y + y, unpacked.z + z))
                    } else if (special.contains(specialBlock.key, Tag.TAG_INT_ARRAY.toInt())) {
                        specialBlocks[specialBlock] = listOf(*special.getIntArray(specialBlock.key).map {
                            val unpacked = BlockPos.of(longs[it])
                            BlockPos(unpacked.x + x, unpacked.y + y, unpacked.z + z)
                        }.toTypedArray())
                    }
                }
                this.specialBlocks =
                    mapOf(*specialBlocks.entries.map { (key, value) -> Pair(key, value) }.toTypedArray())
            } else specialBlocks = mapOf()
        } else blocks = null
    }

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        tag.putBoolean("isAssembled", isAssembled)
        val blocks = blocks ?: return
        val x = blockPos.x
        val y = blockPos.y
        val z = blockPos.z
        val packedBlocks = blocks.stream().map { pos -> BlockPos.asLong(pos.x - x, pos.y - y, pos.z - z) }.toList()
        tag.putLongArray("blocks", packedBlocks)
        if (specialBlocks.isNotEmpty()) {
            val special = CompoundTag()
            for (entry in specialBlocks.entries) {
                if (entry.value.isEmpty()) continue
                else if (entry.value.size == 1) {
                    val pos = entry.value[0]
                    val long = BlockPos.asLong(pos.x - x, pos.y - y, pos.z - z)
                    val index = packedBlocks.indexOf(long)
                    special.putInt(entry.key.key, index)
                } else {
                    val array = mutableListOf<Int>()
                    for (pos in entry.value) {
                        val long = BlockPos.asLong(pos.x - x, pos.y - y, pos.z - z)
                        val index = packedBlocks.indexOf(long)
                        array.add(index)
                    }
                    special.putIntArray(entry.key.key, array)
                }
            }
            tag.put("specialBlocks", special)
        }
    }

    fun onRemove(level: Level) {
        val blocks = blocks ?: return
        if (level is ServerLevel && !level.isClientSide()) {
            val x = blockPos.x
            val y = blockPos.y
            val z = blockPos.z
            val relPos = blocks.map { pos -> BlockPos.asLong(pos.x - x, pos.y - y, pos.z - z) }.toLongArray()

            for (player in level.players()) RPCFunctions.notifyMultiblockDestroyed(
                player, relPos, blockPos
            )
        }
        for (pos in blocks) {
            MultiBlockBlockCache.removeBlock(level, pos)
            level.invalidateCapabilities(pos)
        }
    }

    open fun tileTickCommon(level: Level, pos: BlockPos, state: BlockState) {
    }

    open fun tileTickServer(level: ServerLevel, pos: BlockPos, state: BlockState) {
    }

    open fun tileTickClient(level: ClientLevel, pos: BlockPos, state: BlockState) {
    }

    final override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
    }

    final override fun clientTick(level: ClientLevel, pos: BlockPos, state: BlockState) {
    }

    final override fun commonTick(level: Level, pos: BlockPos, state: BlockState) {
        if (!isAssembled || !isRunning) return
        tileTickCommon(level, pos, state)
        if (level is ServerLevel && !level.isClientSide()) tileTickServer(level, pos, state)
        else if (level is ClientLevel && level.isClientSide()) tileTickClient(level, pos, state)
    }

    override fun tickNoProcessing(level: Level, pos: BlockPos, state: BlockState) {
        if (level is ServerLevel && !level.isClientSide()) {
            if (partsDestroyed.isNotEmpty()) {
                runRecheck()
                partsDestroyed.clear()
                timeUntilNextCheck = 40
                return
            }
            if (isAssembled) return
            if (timeUntilNextCheck > 0) {
                timeUntilNextCheck -= 1
                return
            }
            timeUntilNextCheck = 40

            runRecheck()
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

    /**
     * Notifies the multiblock that a block that's a part of this multiblock was destroyed.
     */
    fun partBlockDestroyed(block: BlockPos) {
        partsDestroyed.add(block)
    }

    private fun runRecheck() {
        val serverLevel = this.levelServer ?: return

        val result = validator().getBlocks(serverLevel, blockPos, getFacingDirection(blockState))
        var affectedBlocks = result?.blocks
        if (affectedBlocks != null) {
            for (block in affectedBlocks) {
                val controllerPos: BlockPos? = MultiBlockBlockCache.getController(serverLevel, block)
                if (controllerPos != null && controllerPos != blockPos) {
                    affectedBlocks = null
                    break
                }
            }
        }

        val old = blocks ?: setOf()
        blocks = null
        val wasPreviouslyAssembled = isAssembled
        isAssembled = affectedBlocks != null && true && !affectedBlocks.isEmpty()
        if (!isAssembled || affectedBlocks == null || result == null) {
            for (pos in old) {
                MultiBlockBlockCache.removeBlock(serverLevel, pos)
                serverLevel.invalidateCapabilities(pos)
            }
            invalidateCapabilities()
            if (!old.isEmpty()) {
                sync()
                val x = blockPos.x
                val y = blockPos.y
                val z = blockPos.z
                val blocks =
                    old.stream().map { p -> BlockPos.asLong(p.x - x, p.y - y, p.z - z) }.toList().toMutableList()
                for (block in partsDestroyed) if (!old.contains(block)) blocks.add(
                    BlockPos.asLong(
                        block.x - x, block.y - y, block.z - z
                    )
                )

                val blocksArray = blocks.toLongArray()
                for (player in serverLevel.players()) RPCFunctions.notifyMultiblockDestroyed(
                    player, blocksArray, blockPos
                )
            }
            return
        }
        for (pos in old) {
            if (pos == blockPos) continue
            if (affectedBlocks.contains(pos)) continue
            MultiBlockBlockCache.removeBlock(serverLevel, pos)
            serverLevel.invalidateCapabilities(pos)
        }

        for (pos in affectedBlocks) {
            if (pos == blockPos) continue

            MultiBlockBlockCache.setController(serverLevel, pos, blockPos)
            serverLevel.invalidateCapabilities(pos)
        }
        invalidateCapabilities()
        blocks = affectedBlocks
        specialBlocks = mapOf(*result.importantBlocks.map { (key, value) -> Pair(key, listOf(*value.toTypedArray())) }
            .toTypedArray())

        if (!old.isEmpty()) {
            val x = blockPos.x
            val y = blockPos.y
            val z = blockPos.z

            val blocks = old.stream().filter { p -> !affectedBlocks.contains(p) }
                .map { p -> BlockPos.asLong(p.x - x, p.y - y, p.z - z) }.toList().toMutableList()

            for (block in partsDestroyed) {
                if (!old.contains(block)) blocks.add(BlockPos.asLong(block.x - x, block.y - y, block.z - z))
            }

            if (blocks.isNotEmpty()) {
                val blocksArray = blocks.toLongArray()
                for (player in serverLevel.players()) RPCFunctions.notifyMultiblockDestroyed(
                    player, blocksArray, blockPos
                )
            }
        }
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

    enum class SpecialBlockType(val key: String) {
        None(""), Ignored(""), EnergyInput("energyIn"), ItemInput("itemIn"), ItemOutput("itemOut"), FluidInput("fluidIn"), FluidOutput(
            "fluidOut"
        );

        companion object {
            val values = entries.filter { it.key.isNotEmpty() }.toList()
        }
    }
}