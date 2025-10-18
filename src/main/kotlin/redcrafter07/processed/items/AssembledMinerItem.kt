package redcrafter07.processed.items

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import redcrafter07.processed.Translations
import redcrafter07.processed.miner.MinerCalc
import redcrafter07.processed.miner.Planetoid
import redcrafter07.processed.rl
import java.lang.Integer.min
import kotlin.jvm.optionals.getOrNull

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
            add(Translations.maxDistance(hull.maxDistance))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.tank).defaultInstance.hoverName)
        if (tank != null) {
            add(Translations.tankCapacity(tank.capacity))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.engine).defaultInstance.hoverName)
        if (engine != null) {
            add(Translations.thrust(engine.thrust))
            add(Translations.efficiency(engine.efficiency))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.miners).defaultInstance.hoverName)
        if (miners != null) {
            add(Translations.miningSpeed(miners.miningSpeed))
            add(Translations.miningFuel(miners.miningFuel))
        }
        tooltip.add(BuiltInRegistries.ITEM.get(assembled.cargoBay).defaultInstance.hoverName)
        if (cargoBay != null) {
            add(Translations.cargoCapacity(cargoBay.capacity))
        }
        tooltip.add(Component.empty())
        tooltip.add(Translations.assembledMinerItemStats())
        if (cargoBay != null && miners != null) {
            val orePerMission = min(cargoBay.capacity, (cargoBay.capacity.toFloat() / miners.miningFuel).toInt())
            val totalMass = listOfNotNull(hull?.mass, tank?.mass, engine?.mass, miners?.mass, cargoBay?.mass).sum();
            add(Translations.mass(totalMass))
            add(Translations.itemYield(orePerMission))
            add(Translations.requiredFuel((miners.miningFuel * orePerMission.toFloat()).toInt()))
            val miningTime = (orePerMission / miners.miningSpeed + 10).toLong() * 60
            add(Translations.miningTime(miningTime))
        }
    }

    fun calc(level: Level, player: Player) {
        val location = player.offhandItem.components.get(ModDataComponents.BOUND_PLANETOID.get()) ?: return
        val planetoid =
            level.registryAccess().registry(Planetoid.REGISTRY_KEY).getOrNull()?.get(location.location) ?: return
        if (planetoid.distance.isEmpty || planetoid.gravity.isEmpty) return
        val dvPerKm = MinerCalc.dvPerKm(planetoid.distance.get())
        val calc = MinerCalc.calculate(
            player.mainHandItem,
            rl("fuel"),
            level.registryAccess(),
            planetoid.distance.get(),
            planetoid.gravity.get().toDouble(),
            2.0,
            dvPerKm
        ) ?: return

        player.sendSystemMessage(Component.literal("required fuel: ").append(Translations.mb(calc.requiredFuel)))
        player.sendSystemMessage(
            Component.literal("flight time (one way): ").append(Translations.duration(calc.flightTimeOneWay * 60))
        )
        player.sendSystemMessage(
            Component.literal("mining time: ").append(Translations.duration(calc.miningTime.toLong() * 60))
        )
        player.sendSystemMessage(Component.literal("mining fuel: ").append(Translations.mb(calc.miningFuel)))
        player.sendSystemMessage(Component.literal("time: ").append(Translations.duration(calc.totalTime * 60)))
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (level.isClientSide && usedHand == InteractionHand.MAIN_HAND) {
            calc(level, player)
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide)
    }
}