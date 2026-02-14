package redcrafter07.processed.transmitters

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import redcrafter07.processed.ProcessedMod
import java.util.UUID
import java.util.function.BiFunction

abstract class TransmitterNetworkData<Network : TransmitterNetwork>(protected val networks: MutableMap<UUID, Network> = HashMap()) :
    SavedData() {
    // All blocks in here either shouldn't have a network or have their network be marked as invalid.
    // That means this doesn't have to be saved, as all the invalid networks will 'disappear' on save.
    // Because of that, all the blocks that had an invalid network will after load still not have a network
    // and as such, ask to be updated again! :>
    private val blocksToUpdate: MutableSet<BlockPos> = HashSet()

    final override fun save(
        tag: CompoundTag, registries: HolderLookup.Provider
    ): CompoundTag {
        for (e in networks) {
            if (e.value.isEmpty() || e.value.invalid) continue
            tag.put(e.key.toString(), e.value.save(CompoundTag()))
        }

        return tag
    }

    protected abstract fun makeNetwork(uuid: UUID): Network

    fun newNetwork(): Network {
        while (true) {
            val id = UUID.randomUUID()
            if (networks.containsKey(id)) continue
            networks[id] = makeNetwork(id)
            return networks[id]!!
        }
    }

    fun getNetwork(id: UUID, pos: BlockPos): Network? {
        val network = networks[id] ?: return null
        if (!network.containsBlock(pos)) return null
        return network
    }

    fun invalidate(block: BlockPos, network: UUID?) {
        if (blocksToUpdate.add(block) && network != null) {
            setDirty()
            // This is in a transmitter network data and this has been set to dirty, so it's fine.
            @Suppress("DEPRECATION") networks[network]?.invalidate()
        }
    }

    protected fun traversedBlock(block: BlockPos) {
        blocksToUpdate.remove(block)
    }

    /**
     * Tries traversing a network.
     */
    protected open fun updateBlock(block: BlockPos, level: ServerLevel) {
        val be = level.getBlockEntity(block) as? TransmitterBlockEntity ?: return
        val network = createOrInvalidNetwork(be.network)
        setDirty()
        // This is in a transmitter network data and this has been set to dirty, so it's fine.
        @Suppress("DEPRECATION") network.invalidate()
        network.invalid = false
        ProcessedMod.LOG.info("updating :>")

        val toScan = arrayListOf(block)
        while (toScan.isNotEmpty()) {
            val pos = toScan.removeFirstOrNull() ?: break
            traversedBlock(pos)
            if (network.containsBlock(pos)) continue
            val be = level.getBlockEntity(pos)
            if (be !is TransmitterBlockEntity) continue

            // This is in a transmitter network data and this has been set to dirty, so it's fine.
            @Suppress("DEPRECATION") network.addBlock(pos)

            be.network = network.id

            val prev = be.connected.value
            be.connected.value = 0
            for (d in Direction.entries) {
                val newPos = pos.relative(d)
                if (!be.isConnected(level, d)) continue
                be.connected[d] = true
                if (level.getBlockState(newPos).`is`(be.blockState.block)) {
                    toScan.add(newPos)
                    continue
                } else {
                    // This is in a transmitter network data and this has been set to dirty, so it's fine.
                    @Suppress("DEPRECATION") network.addEndpoint(pos, d, level)
                }
            }

            level.blockEntityChanged(pos)
            if (prev != be.connected.value) be.sync()
        }
    }

    protected fun createOrInvalidNetwork(network: UUID?): Network {
        if (network == null) return newNetwork()
        val network = networks[network] ?: return newNetwork()
        // This function is to reuse already existing networks and thus already existing memory. If the network isn't invalid,
        // that means that it's in-use and reusing it would be incorrect.
        return if (network.invalid) network else newNetwork()
    }

    fun doTick(level: ServerLevel) {
        if (blocksToUpdate.isEmpty()) return
        // Update all blocks, clear blocks to update, and remove empty and invalid networks.
        while (blocksToUpdate.isNotEmpty()) {
            val block = blocksToUpdate.first()
            traversedBlock(block)
            updateBlock(block, level)
        }
        val invalidNetworks =
            networks.asSequence().filter { it.value.invalid || it.value.isEmpty() }.map { it.key }.toList()
        invalidNetworks.forEach(networks::remove)
        blocksToUpdate.clear()
    }

    class Loader<Network : TransmitterNetwork, NetworkData : TransmitterNetworkData<Network>>(
        val networkDataCtor: (MutableMap<UUID, Network>) -> NetworkData,
        val networkCtor: (UUID, MutableSet<BlockPos>, MutableList<Pair<BlockPos, Direction>>, Int) -> Network
    ) : BiFunction<CompoundTag, HolderLookup.Provider, NetworkData> {
        override fun apply(tag: CompoundTag, ignored: HolderLookup.Provider): NetworkData = load(tag)

        fun load(tag: CompoundTag): NetworkData {
            val networks = HashMap<UUID, Network>()
            for (k in tag.allKeys) {
                try {
                    if (!tag.contains(k, Tag.TAG_COMPOUND.toInt())) continue
                    val id = UUID.fromString(k)
                    val tag = tag.getCompound(k)
                    val network = TransmitterNetwork.load(tag, id, networkCtor) ?: continue
                    networks[id] = network
                } catch (_: IllegalArgumentException) {
                }
            }

            return networkDataCtor(networks)
        }
    }

    companion object {
        fun <Network : TransmitterNetwork, NetworkData : TransmitterNetworkData<Network>> factory(
            networkDataCtor: (MutableMap<UUID, Network>) -> NetworkData,
            networkCtor: (UUID, MutableSet<BlockPos>, MutableList<Pair<BlockPos, Direction>>, Int) -> Network
        ): Factory<NetworkData> = Factory({ networkDataCtor(HashMap()) }, Loader(networkDataCtor, networkCtor))
    }
}