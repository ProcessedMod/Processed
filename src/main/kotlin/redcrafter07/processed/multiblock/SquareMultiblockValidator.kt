package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.world.level.LevelAccessor
import org.joml.Vector3i

// data: flattened list of list of list of part.
// Indexing into it goes as follows:
// index = z + x * size.z + y * (size.x * size.z), meaning that the data is arranged in y=0, x=0, z=0, y=0, x=0, z=1, y=0,x=1, z=0, ...
//
// size: size of the multiblock in blocks, data.size == size.x * size.y * size.z
// restrictions: A list of part matchers and to how many blocks those may only apply and to how many they must at least apply for the structure to be valid.
// This might be something like [Restriction(InputBus,1,1), Restriction(Battery,1,20)].
//
// Additionally, Part.Controller has to appear in data once and only once and never in restrictions.
class SquareMultiblockValidator(val data: List<Part>, val size: Vector3i, val restrictions: List<Restriction>) :
    MultiblockValidator {
    val controllerPosition: BlockPos

    init {
        assert(size.x * size.y * size.z == data.size)
        this.controllerPosition = this.findControllerPosition()
    }

    fun blockAt(x: Int, y: Int, z: Int) = data[y * (size.x * size.z) + x * size.z + size.z - z - 1]

    fun findControllerPosition(): BlockPos {
        for (x in 0..<size.x) {
            for (y in 0..<size.y) {
                for (z in 0..<size.z) {
                    if (blockAt(x, y, z) == Part.Controller) return BlockPos(x, y, z)
                }
            }
        }

        throw IllegalStateException("No controller found")
    }

    private fun isStructureLoaded(level: LevelAccessor, controller: BlockPos, facing: Direction): Boolean {
        val start = controller.relative(facing, controllerPosition.x).relative(Direction.DOWN, controllerPosition.y)
            .relative(facing.clockWise, controllerPosition.z)
        val end = controller.relative(facing, size.x - controllerPosition.x)
            .relative(Direction.DOWN, size.y - controllerPosition.y)
            .relative(facing.clockWise, size.z - controllerPosition.z)

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

    override fun getBlocks(level: LevelAccessor, controller: BlockPos, facing: Direction): MultiblockValidator.Result? {
        // can't scan for the multiblock if it isn't entirely loaded
        if (!isStructureLoaded(level, controller, facing)) return null
        val positions = HashSet<BlockPos>()

        val restrictionOccurrences = restrictions.stream().map { 0 }.toList().toMutableList()
        val specialBlocks = mutableMapOf<MultiblockBlockEntity.SpecialBlockType, BlockPos>()

        for (x in -controllerPosition.x..<size.x - controllerPosition.x) {
            val offsetX = controller.relative(facing, x)
            for (z in -controllerPosition.z..<size.z - controllerPosition.z) {
                val offsetZ = offsetX.relative(facing.clockWise, z)
                for (y in -controllerPosition.y..<size.y - controllerPosition.y) {
                    // controller
                    if (x == 0 && y == 0 && z == 0) continue

                    val part = blockAt(x + controllerPosition.x, y + controllerPosition.y, z + controllerPosition.z)
                    val pos = offsetZ.offset(0, y, 0)
                    val state = level.getBlockState(pos)
                    val specialBlockType = part.blockType(state, level, pos) ?: return null
                    if (specialBlockType != MultiblockBlockEntity.SpecialBlockType.None) specialBlocks[specialBlockType] =
                        pos
                    val controllerPos = MultiBlockBlockCache.getController(level, pos)
                    if (controllerPos != null && controllerPos != controller) return null
                    for (i in 0..<restrictions.size) {
                        if (restrictions[i].part.blockType(state, level, pos) != null) restrictionOccurrences[i]++
                    }

                    positions.add(pos)
                }
            }
        }

        for (i in 0..<restrictions.size) if (restrictionOccurrences[i] < restrictions[i].minRequired || restrictionOccurrences[i] > restrictions[i].maxAllowed) return null

        return MultiblockValidator.Result(positions, specialBlocks)
    }

    class Restriction(val part: Part, val minRequired: Int, val maxAllowed: Int)

    class Builder(val size: Vector3i) {
        constructor(length: Int, height: Int, width: Int) : this(Vector3i(length, height, width))

        private val layers = ArrayList<Part>()
        val restrictions = ArrayList<Restriction>()
        val map = HashMap<Char, Part>()

        fun addMapping(c: Char, part: Part) = apply { map[c] = part }
        fun addLayer(vararg layer: String) = addLayer(layer.toList())
        fun addLayer(layer: List<String>) = apply {
            for (v in layer) for (c in v) {
                val part = map[c] ?: throw IllegalStateException("No mapping for character '$c'")
                layers.add(part)
            }
        }

        fun addRestriction(part: Char, min: Int, max: Int) =
            addRestriction(map[part] ?: throw IllegalStateException("No mapping for character '$part'"), min, max)

        fun addRestriction(part: Part, min: Int, max: Int) = addRestriction(Restriction(part, min, max))
        fun addRestriction(restriction: Restriction) = apply { restrictions.add(restriction) }

        fun build() = SquareMultiblockValidator(layers, size, restrictions)
    }
}