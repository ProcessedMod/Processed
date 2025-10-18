package redcrafter07.processed.events

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BucketItem
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.Translations
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.miner.MinerData
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME)
object MinerComponentTooltipEvent {
    @SubscribeEvent
    fun onTooltip(e: ItemTooltipEvent) {
        if (e.itemStack.has(ModDataComponents.ASSEMBLED_MINER)) return
        val level = e.context.level() ?: return
        val registry = level.registryAccess().registry(MinerData.Fuel.REGISTRY_KEY).getOrNull() ?: return

        val item = e.itemStack.item
        if (item is BucketItem) {
            val key = BuiltInRegistries.FLUID.getKey(item.content)
            val fuel = registry.get(key) ?: return

            e.toolTip.add(1, Translations.specificImpulse(fuel.specificImpulse))
            e.toolTip.add(1, Translations.density(fuel.density))
            return
        }

        val hull = e.itemStack.get(ModDataComponents.HULL_DATA)
        if (hull != null) {
            e.toolTip.add(1, Translations.maxDistance(hull.maxDistance))
            e.toolTip.add(1, Translations.mass(hull.mass))
            return
        }

        val tank = e.itemStack.get(ModDataComponents.TANK_DATA)
        if (tank != null) {
            e.toolTip.add(1, Translations.tankCapacity(tank.capacity))
            e.toolTip.add(1, Translations.mass(tank.mass))
            return
        }

        val engine = e.itemStack.get(ModDataComponents.ENGINE_DATA)
        if (engine != null) {
            e.toolTip.add(1, Translations.efficiency(engine.efficiency))
            e.toolTip.add(1, Translations.thrust(engine.thrust))
            e.toolTip.add(1, Translations.mass(engine.mass))
            return
        }

        val miners = e.itemStack.get(ModDataComponents.MINER_DATA)
        if (miners != null) {
            e.toolTip.add(1, Translations.miningFuel(miners.miningFuel))
            e.toolTip.add(1, Translations.miningSpeed(miners.miningSpeed))
            e.toolTip.add(1, Translations.mass(miners.mass))
            return
        }

        val cargoBay = e.itemStack.get(ModDataComponents.CARGO_BAY_DATA)
        if (cargoBay != null) {
            e.toolTip.add(1, Translations.cargoCapacity(cargoBay.capacity))
            e.toolTip.add(1, Translations.mass(cargoBay.mass))
        }
    }
}