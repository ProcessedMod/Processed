package redcrafter07.processed.events

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.BucketItem
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.Translations
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.miner.MinerCalc
import redcrafter07.processed.miner.MinerData
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME)
object MinerComponentTooltipEvent {
    @SubscribeEvent
    fun onTooltip(e: ItemTooltipEvent) {
        if (e.itemStack.has(ModDataComponents.ASSEMBLED_MINER)) return
        val level = e.context.level() ?: return
        val registry = level.registryAccess().registry(MinerData.Fuel.REGISTRY_KEY).getOrNull() ?: return

        fun add(translation: MutableComponent) = e.toolTip.add(1, translation.withStyle(ChatFormatting.GOLD))

        val item = e.itemStack.item
        if (item is BucketItem) {
            val key = BuiltInRegistries.FLUID.getKey(item.content)
            val fuel = registry.get(key) ?: return

            add(Translations.specificImpulse(fuel.specificImpulse))
            add(Translations.density(MinerCalc.kgPerLiterToKgPerMb(fuel.density)))
            return
        }

        val hull = e.itemStack.get(ModDataComponents.HULL_DATA)
        if (hull != null) {
            add(Translations.maxDistance(hull.maxDistance))
            add(Translations.mass(hull.mass))
            return
        }

        val tank = e.itemStack.get(ModDataComponents.TANK_DATA)
        if (tank != null) {
            add(Translations.tankCapacity(MinerCalc.literToMb(tank.capacity)))
            add(Translations.mass(tank.mass))
            return
        }

        val engine = e.itemStack.get(ModDataComponents.ENGINE_DATA)
        if (engine != null) {
            add(Translations.efficiency(engine.efficiency))
            add(Translations.thrust(engine.thrust))
            add(Translations.mass(engine.mass))
            val fluid = FluidStack(BuiltInRegistries.FLUID.get(engine.fuel), 1).hoverName
            add(Translations.fuel(fluid))
            return
        }

        val miners = e.itemStack.get(ModDataComponents.MINER_DATA)
        if (miners != null) {
            add(Translations.miningSpeed(miners.miningSpeed))
            add(Translations.mass(miners.mass))
            return
        }

        val cargoBay = e.itemStack.get(ModDataComponents.CARGO_BAY_DATA)
        if (cargoBay != null) {
            add(Translations.cargoCapacity(cargoBay.capacity))
            add(Translations.mass(cargoBay.mass))
        }
    }
}