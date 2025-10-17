package redcrafter07.processed.items

import net.minecraft.core.component.DataComponentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.items.LocationSelectorItem.BoundPlanetoid
import java.util.function.Supplier
import java.util.function.UnaryOperator

object ModDataComponents {
    val DATA_COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(ProcessedMod.ID)
    val WRENCH_MODE =
        register("wrench_mode") { it.persistent(WrenchMode.CODEC).networkSynchronized(WrenchMode.STREAM_CODEC) }
    val MINER_DATA =
        register("miner_data") { it.persistent(MinerItem.CODEC).networkSynchronized(MinerItem.STREAM_CODEC) }
    val ENGINE_TIER =
        register("engine_tier") { it.persistent(ProcessedTier.CODEC).networkSynchronized(ProcessedTier.STREAM_CODEC) }
    val BOUND_PLANETOID = register("bound_planetoid") {
        it.persistent(BoundPlanetoid.CODEC).networkSynchronized(BoundPlanetoid.STREAM_CODEC)
    }

    private fun <T> register(
        name: String, provider: UnaryOperator<DataComponentType.Builder<T>>
    ): DeferredHolder<DataComponentType<*>, DataComponentType<T>> {
        return DATA_COMPONENTS.register(
            name, Supplier { provider.apply(DataComponentType.builder()).build() })
    }
}