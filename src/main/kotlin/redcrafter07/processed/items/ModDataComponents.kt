package redcrafter07.processed.items

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.items.LocationSelectorItem.BoundPlanetoid
import redcrafter07.processed.miner.MinerData
import java.util.function.Supplier
import java.util.function.UnaryOperator

object ModDataComponents {
    val DATA_COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ProcessedMod.ID)
    val WRENCH_MODE =
        register("wrench_mode") { it.persistent(WrenchMode.CODEC).networkSynchronized(WrenchMode.STREAM_CODEC) }
    val BOUND_PLANETOID = register("bound_planetoid") {
        it.persistent(BoundPlanetoid.CODEC).networkSynchronized(BoundPlanetoid.STREAM_CODEC)
    }

    val ASSEMBLED_MINER = register("assembled_miner") {
        it.persistent(MinerData.Assembled.CODEC).networkSynchronized(MinerData.Assembled.STREAM_CODEC)
    }
    val HULL_DATA =
        register("hull_data") { it.persistent(MinerData.Hull.CODEC).networkSynchronized(MinerData.Hull.STREAM_CODEC) }
    val TANK_DATA =
        register("tank_data") { it.persistent(MinerData.Tank.CODEC).networkSynchronized(MinerData.Tank.STREAM_CODEC) }
    val ENGINE_DATA = register("engine_data") {
        it.persistent(MinerData.Engine.CODEC).networkSynchronized(MinerData.Engine.STREAM_CODEC)
    }
    val MINER_DATA = register("miner_data") {
        it.persistent(MinerData.Miners.CODEC).networkSynchronized(MinerData.Miners.STREAM_CODEC)
    }
    val CARGO_BAY_DATA = register("cargo_bay_data") {
        it.persistent(MinerData.CargoBay.CODEC).networkSynchronized(MinerData.CargoBay.STREAM_CODEC)
    }

    private fun <T> register(
        name: String, provider: UnaryOperator<DataComponentType.Builder<T>>
    ): DeferredHolder<DataComponentType<*>, DataComponentType<T>> {
        return DATA_COMPONENTS.register(
            name, Supplier { provider.apply(DataComponentType.builder()).build() })
    }
}