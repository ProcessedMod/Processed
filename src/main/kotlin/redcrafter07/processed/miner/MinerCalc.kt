package redcrafter07.processed.miner

import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.openjdk.nashorn.internal.objects.NativeMath.LN2
import redcrafter07.processed.items.ModDataComponents
import java.lang.Integer.min
import kotlin.jvm.optionals.getOrNull
import kotlin.math.exp
import kotlin.math.max

object MinerCalc {
    fun dvPerKm(distance: Long): Double = max(.009, 2 * exp(-LN2 * (distance / 2000.toDouble())))

    /** distance: km, gravity: m/s², density: kg/block, dvPerKm: m/s/km */
    fun calculate(
        assembled: ItemStack,
        fuel: ResourceLocation,
        registries: RegistryAccess,
        distance: Long,
        gravity: Double,
        blockDensity: Double,
        dvPerKm: Double
    ): Result? {
        val fuel = registries.registry(MinerData.Fuel.REGISTRY_KEY).getOrNull()?.get(fuel) ?: return null
        val hull = assembled.get(ModDataComponents.HULL_DATA) ?: return null
        val tank = assembled.get(ModDataComponents.TANK_DATA) ?: return null
        val engine = assembled.get(ModDataComponents.ENGINE_DATA) ?: return null
        val miners = assembled.get(ModDataComponents.MINER_DATA) ?: return null
        val cargoBay = assembled.get(ModDataComponents.CARGO_BAY_DATA) ?: return null
        val orePerMission = min(cargoBay.capacity, (cargoBay.capacity.toFloat() / miners.miningFuel).toInt())

        // intermediaries
        val structureMass = (hull.mass + tank.mass + engine.mass + miners.mass + cargoBay.mass).toDouble() // B22
        val propellantMassFull = (tank.capacity.toDouble() * fuel.density.toDouble()).toLong() // B23
        val minerFuelVolumeReq = (orePerMission.toDouble() * miners.miningFuel.toDouble()).toLong() // B24
        val minerFuelMass = minerFuelVolumeReq.toDouble() * fuel.density.toDouble() // B25
        val oreMass = orePerMission.toDouble() * blockDensity // B26
        val liftoffMass = structureMass + propellantMassFull + minerFuelMass // B27
        val dvOneWay = distance.toDouble() * dvPerKm // B28

        // Outbound Burn 1
        val effectiveSpecificImpulse = fuel.specificImpulse.toLong() * engine.efficiency.toLong() / 100 // B30
        val outboundFuelMass =
            liftoffMass * (1 - exp(-dvOneWay / (effectiveSpecificImpulse.toDouble() * gravity))) // B31
        val outboundFuelVolume = outboundFuelMass / fuel.density.toDouble() // B32
        val massAtTargetBeforeMining = liftoffMass - outboundFuelMass // B33
        val minerFuelConsumed = minerFuelMass // B34
        val massAtTargetAfterMining = massAtTargetBeforeMining - minerFuelConsumed + oreMass // B35

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
        val flightTimeOneWayMinutes = dvOneWay / avgAccel / 60 // B46
        val miningTimeMinutes = (orePerMission.toDouble() / miners.miningSpeed.toDouble()) + 10 // B47
        val totalMissionTimeMinutes = flightTimeOneWayMinutes * 2 + miningTimeMinutes // B48

        return Result(
            requiredFuel.toInt(),
            flightTimeOneWayMinutes.toLong(),
            miningTimeMinutes.toInt(),
            minerFuelVolumeReq.toInt(),
            totalMissionTimeMinutes.toLong()
        )
    }

    /** requiredFuel: mB, flightTimeOneWay: minutes, miningTime: minutes, miningFuel: mB, totalTime: minutes */
    data class Result(
        val requiredFuel: Int, val flightTimeOneWay: Long, val miningTime: Int, val miningFuel: Int, val totalTime: Long
    )
}