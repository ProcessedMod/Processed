package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.ChunkAccess
import redcrafter07.processed.Attachments

class MultiBlockCasingCache {
    companion object {
        fun remove(access: ChunkAccess) = access.removeData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)


        fun getOrDefault(access: ChunkAccess): MultiBlockCasingCache = access.getData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)

        fun get(access: ChunkAccess): MultiBlockCasingCache? {
            if (!access.hasData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)) return null
            return access.getData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)
        }


        fun getControllerForCasing(level: LevelAccessor, casing: BlockPos): BlockPos? {
            val chunkAccess = level.getChunk(
                SectionPos.blockToSectionCoord(casing.x), SectionPos.blockToSectionCoord(casing.z)
            )
            val cache = get(chunkAccess)
            if (cache != null && cache.multiblockMap.containsKey(casing)) return cache.multiblockMap.get(casing)
            return null
        }

        fun removeCasing(level: LevelAccessor, casing: BlockPos) {
            val chunkAccess = level.getChunk(
                SectionPos.blockToSectionCoord(casing.x), SectionPos.blockToSectionCoord(casing.z)
            )
            val cache = get(chunkAccess) ?: return
            if (cache.multiblockMap.containsKey(casing)) {
                cache.multiblockMap.remove(casing)
                cache.set(chunkAccess)
            }
        }

        fun setControllerForCasing(level: LevelAccessor, casing: BlockPos, controller: BlockPos) {
            val chunkAccess = level.getChunk(
                SectionPos.blockToSectionCoord(casing.x), SectionPos.blockToSectionCoord(casing.z)
            )
            val cache = getOrDefault(chunkAccess)
            cache.multiblockMap[casing] = controller
            cache.set(chunkAccess)
        }
    }

    val multiblockMap: HashMap<BlockPos, BlockPos> = HashMap()

    fun set(access: ChunkAccess) {
        if (multiblockMap.isEmpty()) remove(access)
        else access.setData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT, this)
    }

    override fun toString(): String {
        val builder = StringBuilder("{\n")
        for (entry in multiblockMap.entries) {
            builder.append("    ")
            builder.append(entry.key)
            builder.append(" => ")
            builder.append(entry.value)
            builder.append(",\n")
        }
        builder.append('}')
        return builder.toString()
    }
}