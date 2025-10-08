package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import org.joml.Vector2i

/*

- z
| x
\n y

fff
fff
fff

fff
c f
fff

fff
fff
fff

*/
class BlockMapMultiblockValidator(
    val blockMap: Map<Block, MultiblockPart>, val blockData: List<List<MultiblockPart>>, val size: Vector2i
) : MultiblockValidator {
    val controllerPosition: BlockPos

    init {
        this.controllerPosition = this.findControllerPosition()
    }

    fun getBlockAt(pos: BlockPos): MultiblockPart {
        return blockData[pos.y][pos.x * size.x + pos.z]
    }

    fun findControllerPosition(): BlockPos {
        for (y in blockData.indices) {
            for (x in 0..<size.x) {
                for (z in 0..<size.y) {
                    if (getBlockAt(BlockPos(x, y, z)) == MultiblockPart.Controller) {
                        return BlockPos(x, y, z)
                    }
                }
            }
        }

        return BlockPos.ZERO
    }

    private fun isStructureLoaded(level: LevelAccessor, controller: BlockPos, facing: Direction): Boolean {
        val start = controller.relative(facing, controllerPosition.x).relative(Direction.DOWN, controllerPosition.y)
            .relative(facing.clockWise, controllerPosition.z)
        val end = controller.relative(facing, size.x - controllerPosition.x)
            .relative(Direction.DOWN, blockData.size - controllerPosition.y)
            .relative(facing.clockWise, size.y - controllerPosition.z)

        val startChunkX = SectionPos.blockToSectionCoord(start.x)
        val startChunkZ = SectionPos.blockToSectionCoord(start.z)
        val endChunkX = SectionPos.blockToSectionCoord(end.x)
        val endChunkZ = SectionPos.blockToSectionCoord(end.z)

        for (x in startChunkX..endChunkX) {
            for (z in startChunkZ..endChunkZ) {
                if (!level.hasChunk(x, z)) return false
            }
        }
        return true
    }

    override fun getBlocks(level: LevelAccessor, controller: BlockPos, facing: Direction): MutableSet<BlockPos>? {
        // can't scan for the multiblock if it isn't entirely loaded
        if (!isStructureLoaded(level, controller, facing)) return null
        val positions = HashSet<BlockPos>()

        for (x in -controllerPosition.x..<size.x - controllerPosition.x) {
            val offsetX = controller.relative(facing, x)
            for (z in -controllerPosition.z..<size.y - controllerPosition.z) {
                val offsetZ = offsetX.relative(facing.clockWise, z)
                for (y in -controllerPosition.y..<blockData.size - controllerPosition.y) {
                    val expected = getBlockAt(
                        BlockPos(
                            x + controllerPosition.x, y + controllerPosition.y, z + controllerPosition.z
                        )
                    )
                    if (expected == MultiblockPart.Ignored) continue

                    val pos = offsetZ.offset(0, y, 0)
                    val foundState = level.getBlockState(pos)
                    val found = blockMap[foundState.block]
                    if (found == null || found != expected) return null
                    val controllerPos: BlockPos? = MultiBlockCasingCache.getControllerForCasing(level, pos)
                    if (controllerPos != null && controllerPos !== controller) return null
                    positions.add(pos)
                }
            }
        }

        return positions
    }
}