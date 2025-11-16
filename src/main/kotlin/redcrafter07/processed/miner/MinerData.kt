package redcrafter07.processed.miner

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.Registry
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import redcrafter07.processed.rl

object MinerData {
    data class Assembled(
        val hull: ResourceLocation,
        val tank: ResourceLocation,
        val engine: ResourceLocation,
        val miners: ResourceLocation,
        val cargoBay: ResourceLocation
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    ResourceLocation.CODEC.fieldOf("hull").forGetter(Assembled::hull),
                    ResourceLocation.CODEC.fieldOf("tank").forGetter(Assembled::tank),
                    ResourceLocation.CODEC.fieldOf("engine").forGetter(Assembled::engine),
                    ResourceLocation.CODEC.fieldOf("miners").forGetter(Assembled::miners),
                    ResourceLocation.CODEC.fieldOf("cargoBay").forGetter(Assembled::cargoBay),
                ).apply(it, ::Assembled)
            }
            val STREAM_CODEC: StreamCodec<ByteBuf, Assembled> = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                Assembled::hull,
                ResourceLocation.STREAM_CODEC,
                Assembled::tank,
                ResourceLocation.STREAM_CODEC,
                Assembled::engine,
                ResourceLocation.STREAM_CODEC,
                Assembled::miners,
                ResourceLocation.STREAM_CODEC,
                Assembled::cargoBay,
                ::Assembled
            )
        }
    }

    /** mass: kg, maxDistance: km */
    data class Hull(
        /** kg */
        val mass: Int,
        /** km */
        val maxDistance: Int
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    Codec.INT.fieldOf("mass").forGetter(Hull::mass),
                    Codec.INT.fieldOf("maxDistance").forGetter(Hull::maxDistance),
                ).apply(it, ::Hull)
            }
            val STREAM_CODEC = sc(ByteBufCodecs.INT, Hull::mass, ByteBufCodecs.INT, Hull::maxDistance, ::Hull)
        }
    }

    /** mass: kg, capacity: L */
    data class Tank(
        /** kg */
        val mass: Int,
        /** L */
        val capacity: Int
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    Codec.INT.fieldOf("mass").forGetter(Tank::mass),
                    Codec.INT.fieldOf("capacity").forGetter(Tank::capacity),
                ).apply(it, ::Tank)
            }
            val STREAM_CODEC = sc(ByteBufCodecs.INT, Tank::mass, ByteBufCodecs.INT, Tank::capacity, ::Tank)
        }
    }

    /** density: kg/L, specificImpulse/Iₛₚ: s (seconds) */
    data class Fuel(
        /** kg/L */
        val density: Float,
        /** s (seconds) */
        val specificImpulse: Int
    ) {
        companion object {
            // The registry key of all fuels that were registered by a datapack.
            // This is registered in the `DataPackRegistryEvent.NewRegistry` event.
            val REGISTRY_KEY: ResourceKey<Registry<Fuel>> = ResourceKey.createRegistryKey(rl("fuel"))

            val CODEC = c {
                it.group(
                    Codec.FLOAT.fieldOf("density").forGetter(Fuel::density),
                    Codec.INT.fieldOf("specificImpulse").forGetter(Fuel::specificImpulse)
                ).apply(it, ::Fuel)
            }
        }
    }

    /** Mass: kg, Thrust: N, Efficiency: Percent (%) */
    data class Engine(
        /** kg */
        val mass: Int,
        /** N */
        val thrust: Int,
        /** Percent (%) */
        val efficiency: Int
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    Codec.INT.fieldOf("mass").forGetter(Engine::mass),
                    Codec.INT.fieldOf("thrust").forGetter(Engine::thrust),
                    Codec.INT.fieldOf("efficiency").forGetter(Engine::efficiency),
                ).apply(it, ::Engine)
            }
            val STREAM_CODEC = sc(
                ByteBufCodecs.INT,
                Engine::mass,
                ByteBufCodecs.INT,
                Engine::thrust,
                ByteBufCodecs.INT,
                Engine::efficiency,
                ::Engine
            )
        }
    }

    /** Mass: kg, Mining speed: blocks/min, Mining Fuel: L/block */
    data class Miners(
        /** kg */
        val mass: Int,
        /** blocks/min */
        val miningSpeed: Int,
        /** L/block */
        val miningFuel: Float
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    Codec.INT.fieldOf("mass").forGetter(Miners::mass),
                    Codec.INT.fieldOf("miningSpeed").forGetter(Miners::miningSpeed),
                    Codec.FLOAT.fieldOf("miningFuel").forGetter(Miners::miningFuel),
                ).apply(it, ::Miners)
            }
            val STREAM_CODEC = sc(
                ByteBufCodecs.INT,
                Miners::mass,
                ByteBufCodecs.INT,
                Miners::miningSpeed,
                ByteBufCodecs.FLOAT,
                Miners::miningFuel,
                ::Miners
            )
        }
    }

    /** Mass: kg, Capacity: L or Items */
    data class CargoBay(
        /** kg */
        val mass: Int,
        /** L or Items */
        val capacity: Int
    ) {
        companion object {
            val CODEC = c {
                it.group(
                    Codec.INT.fieldOf("mass").forGetter(CargoBay::mass),
                    Codec.INT.fieldOf("capacity").forGetter(CargoBay::capacity),
                ).apply(it, ::CargoBay)
            }
            val STREAM_CODEC = sc(ByteBufCodecs.INT, CargoBay::mass, ByteBufCodecs.INT, CargoBay::capacity, ::CargoBay)
        }
    }

    fun <T> c(f: (RecordCodecBuilder.Instance<T>) -> App<RecordCodecBuilder.Mu<T>, T>): Codec<T> =
        RecordCodecBuilder.create(f)

    fun <C, T1, T2> sc(
        c1: StreamCodec<ByteBuf, T1>, g1: (C) -> T1, c2: StreamCodec<ByteBuf, T2>, g2: (C) -> T2, f: (T1, T2) -> C
    ): StreamCodec<ByteBuf, C> = StreamCodec.composite(c1, g1, c2, g2, f)

    fun <C, T1, T2, T3> sc(
        c1: StreamCodec<ByteBuf, T1>,
        g1: (C) -> T1,
        c2: StreamCodec<ByteBuf, T2>,
        g2: (C) -> T2,
        c3: StreamCodec<ByteBuf, T3>,
        g3: (C) -> T3,
        f: (T1, T2, T3) -> C
    ): StreamCodec<ByteBuf, C> = StreamCodec.composite(c1, g1, c2, g2, c3, g3, f)
}