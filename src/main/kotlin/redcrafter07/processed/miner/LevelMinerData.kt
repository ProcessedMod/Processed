package redcrafter07.processed.miner

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import redcrafter07.processed.Attachments
import java.time.Instant
import java.util.*
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

class LevelMinerData(private val launchedMiners: HashMap<UUID, LaunchedMinerData> = HashMap()) {
    var nextArrival: Instant? = null
        private set

    private fun computeNextInstant() {
        var nextInstant: Instant? = null
        for (v in launchedMiners.values) {
            if (nextInstant == null) nextInstant = v.arrival
            else if (nextInstant.isAfter(v.arrival)) nextInstant = v.arrival
        }
        this.nextArrival = nextInstant
    }

    fun arrived(): Stream<UUID> {
        val now = Instant.now().epochSecond
        return launchedMiners.entries.stream().filter { (_, v) -> v.arrivalEpoch <= now }.map { (k, _) -> k }
    }

    init {
        computeNextInstant()
    }

    operator fun get(uuid: UUID): LaunchedMinerData? = launchedMiners[uuid]
    fun put(data: LaunchedMinerData): UUID {
        var uuid = UUID.randomUUID()
        while (launchedMiners[uuid] != null) uuid = UUID.randomUUID()
        launchedMiners[uuid] = data
        if (nextArrival.run { this?.isAfter(data.arrival) ?: true }) nextArrival = data.arrival
        return uuid
    }

    fun remove(uuid: UUID) {
        val data = launchedMiners.remove(uuid) ?: return
        val instant = nextArrival
        if (instant == null || instant.epochSecond >= data.arrival.epochSecond) computeNextInstant()
    }

    companion object {
        fun put(level: ServerLevel, data: LaunchedMinerData) = level.getData(Attachments.LEVEL_MINER_DATA).put(data)

        fun get(level: ServerLevel, uuid: UUID) =
            level.getExistingData(Attachments.LEVEL_MINER_DATA).getOrNull()?.get(uuid)

        fun getOrDefault(level: ServerLevel): LevelMinerData = level.getData(Attachments.LEVEL_MINER_DATA)

        fun shouldDoWork(level: ServerLevel): Boolean {
            val data = level.getExistingData(Attachments.LEVEL_MINER_DATA).getOrNull() ?: return false
            val epochTime = data.nextArrival ?: return false
            return Instant.now().epochSecond >= epochTime.epochSecond
        }

        val LAUNCHER_MINER_DATA_CODEC: Codec<LaunchedMinerData> = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(LaunchedMinerData::item),
                Codec.INT.fieldOf("itemAmount").forGetter(LaunchedMinerData::itemAmount),
                Codec.LONG.fieldOf("arrivalEpoch").forGetter(LaunchedMinerData::arrivalEpoch),
                Codec.LONG.fieldOf("planetArrival").forGetter(LaunchedMinerData::planetArrivalEpoch),
                Codec.LONG.fieldOf("miningFinish").forGetter(LaunchedMinerData::miningFinishEpoch),
                BlockPos.CODEC.fieldOf("controller").forGetter(LaunchedMinerData::controllerPos),
            ).apply(it, ::LaunchedMinerData)
        }
        val CODEC: Codec<LevelMinerData> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, LAUNCHER_MINER_DATA_CODEC).xmap({
            val map = HashMap<UUID, LaunchedMinerData>()
            for (entry in it.entries) map[entry.key] = entry.value
            LevelMinerData(map)
        }, LevelMinerData::launchedMiners)
    }

    class LaunchedMinerData(
        val item: ResourceLocation,
        val itemAmount: Int,
        val arrival: Instant,
        val planetArrival: Instant,
        val miningFinish: Instant,
        val controllerPos: BlockPos,
    ) {
        constructor(
            oreItem: ResourceLocation,
            itemAmount: Int,
            arrivalEpoch: Long,
            planetArrivalEpoch: Long,
            miningFinishEpoch: Long,
            controllerPos: BlockPos,
        ) : this(
            oreItem,
            itemAmount,
            Instant.ofEpochSecond(arrivalEpoch),
            Instant.ofEpochSecond(planetArrivalEpoch),
            Instant.ofEpochSecond(miningFinishEpoch),
            controllerPos,
        )

        val arrivalEpoch: Long get() = arrival.epochSecond
        val planetArrivalEpoch: Long get() = planetArrival.epochSecond
        val miningFinishEpoch: Long get() = miningFinish.epochSecond

        companion object {
            val STREAM_CODEC: StreamCodec<ByteBuf, LaunchedMinerData> = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                LaunchedMinerData::item,
                ByteBufCodecs.INT,
                LaunchedMinerData::itemAmount,
                ByteBufCodecs.VAR_LONG,
                LaunchedMinerData::arrivalEpoch,
                ByteBufCodecs.VAR_LONG,
                LaunchedMinerData::planetArrivalEpoch,
                ByteBufCodecs.VAR_LONG,
                LaunchedMinerData::miningFinishEpoch,
                BlockPos.STREAM_CODEC,
                LaunchedMinerData::controllerPos,
                ::LaunchedMinerData
            )
        }
    }
}