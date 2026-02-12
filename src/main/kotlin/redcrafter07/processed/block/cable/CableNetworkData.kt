package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import redcrafter07.processed.ProcessedMod
import java.util.UUID

class CableNetworkData(private val networks: MutableMap<UUID, CableNetwork> = HashMap()) : SavedData() {
    override fun save(
        tag: CompoundTag, registries: HolderLookup.Provider
    ): CompoundTag {
        for (e in networks) {
            if (e.value.isEmpty()) continue
            tag.put(e.key.toString(), e.value.save(CompoundTag()))
        }

        return tag
    }

    fun remove(id: UUID) {
        if (networks.remove(id) != null) setDirty()
    }

    fun newNetwork(): CableNetwork {
        while (true) {
            val id = UUID.randomUUID()
            if (networks.containsKey(id)) continue
            networks[id] = CableNetwork(id)
            return networks[id]!!
        }
    }

    fun getNetwork(id: UUID, pos: BlockPos): CableNetwork? {
        val network = networks[id] ?: return null
        if (!network.containsBlock(pos)) return null
        return network
    }

    companion object {
        private fun load(tag: CompoundTag, ignored: HolderLookup.Provider): CableNetworkData {
            val networks = HashMap<UUID, CableNetwork>()
            for (k in tag.allKeys) {
                try {
                    if (!tag.contains(k, Tag.TAG_COMPOUND.toInt())) continue
                    val id = UUID.fromString(k)
                    val tag = tag.getCompound(k)
                    val network = PipeLikeNetwork.load(tag, id, ::CableNetwork) ?: continue
                    networks[id] = network
                } catch (_: IllegalArgumentException) {
                }
            }

            return CableNetworkData(networks)
        }

        private val FACTORY = Factory(::CableNetworkData, ::load)
        private const val NAME = ProcessedMod.ID + ".cable_networks"
        fun getOrNull(level: ServerLevel): CableNetworkData? = level.dataStorage.get(FACTORY, NAME)
        fun getOrMake(level: ServerLevel): CableNetworkData = level.dataStorage.computeIfAbsent(FACTORY, NAME)
        fun getNetwork(level: ServerLevel, id: UUID, pos: BlockPos): CableNetwork? =
            getOrNull(level)?.getNetwork(id, pos)
    }
}