package redcrafter07.processed.items

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.Translations

class AssembledMinerItem : Item(Properties().stacksTo(1)) {
    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
        val assembled = stack.get(ModDataComponents.ASSEMBLED_MINER) ?: return
        fun add(c: Component) = tooltip.add(Component.literal("  ").append(c).withStyle(ChatFormatting.DARK_GRAY))

        val hull = stack.get(ModDataComponents.HULL_DATA)
        val tank = stack.get(ModDataComponents.TANK_DATA)
        val engine = stack.get(ModDataComponents.ENGINE_DATA)
        val miners = stack.get(ModDataComponents.MINER_DATA)
        val cargoBay = stack.get(ModDataComponents.CARGO_BAY_DATA)

        tooltip.add(Translations.assembledMinerItemComponents())
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.hull).defaultInstance.hoverName)
        if (hull != null) {
            add(Translations.mass(hull.mass))
            add(Translations.maxDistance(hull.maxDistance))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.tank).defaultInstance.hoverName)
        if (tank != null) {
            add(Translations.mass(tank.mass))
            add(Translations.tankCapacity(tank.capacity))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.engine).defaultInstance.hoverName)
        if (engine != null) {
            add(Translations.mass(engine.mass))
            add(Translations.thrust(engine.thrust))
            add(Translations.efficiency(engine.efficiency))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.miners).defaultInstance.hoverName)
        if (miners != null) {
            add(Translations.mass(miners.mass))
            add(Translations.miningSpeed(miners.miningSpeed))
            add(Translations.miningFuel(miners.miningFuel))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.cargoBay).defaultInstance.hoverName)
        if (cargoBay != null) {
            add(Translations.mass(cargoBay.mass))
            add(Translations.cargoCapacity(cargoBay.capacity))
        }
    }
}