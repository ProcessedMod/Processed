package redcrafter07.processed.miner

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.openjdk.nashorn.internal.objects.NativeMath.LN2
import redcrafter07.processed.items.ModDataComponents
import kotlin.jvm.optionals.getOrNull
import kotlin.math.exp
import kotlin.math.max

object MinerCalc {
    private fun dvPerKm(distance: Long): Double = max(.009, 2 * exp(-LN2 * (distance / 2000.toDouble())))

    fun literToMb(l: Int) = l * 10
    fun kgPerLiterToKgPerMb(kgPerL: Float) = kgPerL / 10f
    // TODO: Make this configurable
    /** density: kg/block */
    const val DEFAULT_DENSITY: Double = 2.0

    /** density: kg/block */
    fun calculate(
        assembled: ItemStack, destination: Planetoid, blockDensity: Double = DEFAULT_DENSITY
    ): Result? {
        val distance = destination.distance.getOrNull() ?: return null
        val gravity = destination.gravity.getOrNull() ?: return null
        return calculate(assembled, distance, gravity.toDouble(), blockDensity)
    }

    /** distance: km, gravity: m/s², density: kg/block, dvPerKm: m/s/km */
    fun calculate(
        assembled: ItemStack,
        distance: Long,
        gravity: Double,
        blockDensity: Double,
    ): Result? {
        val dvPerKm = dvPerKm(distance)
        val hull = assembled.get(ModDataComponents.HULL_DATA) ?: return null
        val tank = assembled.get(ModDataComponents.TANK_DATA) ?: return null
        val engine = assembled.get(ModDataComponents.ENGINE_DATA) ?: return null
        val miners = assembled.get(ModDataComponents.MINER_DATA) ?: return null
        val cargoBay = assembled.get(ModDataComponents.CARGO_BAY_DATA) ?: return null
        val orePerMission = cargoBay.capacity

        val holder = NeoForgeRegistries.FLUID_TYPES.getHolder(engine.fuel).getOrNull() ?: return null
        val fuel = holder.getData(MinerData.Fuel.DATA_MAP) ?: return null

        // intermediaries
        val structureMass = (hull.mass + tank.mass + engine.mass + miners.mass + cargoBay.mass).toDouble() // B22
        val propellantMassFull = (tank.capacity.toDouble() * fuel.density.toDouble()).toLong() // B23
        val oreMass = orePerMission.toDouble() * blockDensity // B26
        val liftoffMass = structureMass + propellantMassFull // B27
        val dvOneWay = distance.toDouble() * dvPerKm // B28

        // Outbound Burn 1
        val effectiveSpecificImpulse = fuel.specificImpulse.toLong() * engine.efficiency.toLong() / 100 // B30
        val outboundFuelMass =
            liftoffMass * (1 - exp(-dvOneWay / (effectiveSpecificImpulse.toDouble() * gravity))) // B31
        val outboundFuelVolume = outboundFuelMass / fuel.density.toDouble() // B32
        val massAtTargetBeforeMining = liftoffMass - outboundFuelMass // B33
        val massAtTargetAfterMining = massAtTargetBeforeMining + oreMass // B35

        // Outbound Burn 2
        val returnTakeoffMass = massAtTargetAfterMining // B36
        val returnFuelMass = returnTakeoffMass * (1 - exp(-dvOneWay / (effectiveSpecificImpulse * gravity))) // B37
        val returnFuelVolume = returnFuelMass / fuel.density.toDouble() // B38
        // B39 is unused
        val totalRequiredPropellantVolume = outboundFuelVolume + returnFuelVolume // B40

        // Result
        // B42 and B43 are unused
        val requiredFuel = totalRequiredPropellantVolume // B44
        val avgAccel = engine.thrust.toDouble() / ((liftoffMass + returnFuelMass) / 2) // B45
        val flightTimeOneWayMinutes =
            dvOneWay / avgAccel / 60 + 0.5 // B46, +0.5 for launch animation (which should have finished in 30 seconds)
        val miningTimeMinutes = (orePerMission.toDouble() / miners.miningSpeed.toDouble()) // B47
        val totalMissionTimeMinutes = flightTimeOneWayMinutes * 2 + miningTimeMinutes // B48

        return Result(
            literToMb(requiredFuel.toInt()), flightTimeOneWayMinutes, miningTimeMinutes, totalMissionTimeMinutes
        )
    }

    /** requiredFuel: mB, flightTimeOneWay: minutes, miningTime: minutes, totalTime: minutes */
    data class Result(
        val requiredFuel: Int, val flightTimeOneWay: Double, val miningTime: Double, val totalTime: Double
    ) {
        companion object {
            val STREAM_CODEC: StreamCodec<ByteBuf, Result> = StreamCodec.composite(
                ByteBufCodecs.INT,
                Result::requiredFuel,
                ByteBufCodecs.DOUBLE,
                Result::flightTimeOneWay,
                ByteBufCodecs.DOUBLE,
                Result::miningTime,
                ByteBufCodecs.DOUBLE,
                Result::totalTime,
                ::Result
            )
        }
    }
}