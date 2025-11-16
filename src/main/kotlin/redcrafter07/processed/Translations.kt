@file:Suppress("NOTHING_TO_INLINE")

package redcrafter07.processed

import net.neoforged.neoforge.common.TranslatableEnum
import org.apache.commons.lang3.time.DurationFormatUtils
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.items.WrenchMode
import redcrafter07.processed.materials.Material
import java.util.concurrent.TimeUnit
import net.minecraft.network.chat.Component as C
import net.minecraft.network.chat.MutableComponent as MC

object Translations {
    inline fun materialDust(material: Material) = t("processed.material_dust", material)
    inline fun materialIngot(material: Material) = t("processed.material_ingot", material)
    inline fun materialNugget(material: Material) = t("processed.material_nugget", material)
    inline fun materialRaw(material: Material) = t("processed.material_raw", material)
    inline fun materialMetalBlock(material: Material) = t("processed.material_metal_block", material)
    inline fun materialOre(material: Material) = t("processed.material_ore", material)
    inline fun materialRawMetalBlock(material: Material) = t("processed.material_raw_metal_block", material)
    inline fun materialCable(material: Material) = t("processed.material_cable", material)
    inline fun materialItemPipe(material: Material) = t("processed.material_item_pipe", material)
    inline fun materialName(identifier: String) = t("processed.material.$identifier")
    inline fun blockItemTooltip(id: String) = t("block.processed.$id.tooltip")
    inline fun itemTooltip(id: String) = t("item.processed.$id.tooltip")
    inline fun wrenchMode(modeName: String) = t("item.processed.wrench.mode.$modeName")
    inline fun wrenchModeTooltip(mode: WrenchMode) = t("item.processed.wrench.mode", mode)

    inline fun mainItemGroup() = t("item_group.processed.main")
    inline fun materialsItemGroup() = t("item_group.processed.materials")

    inline fun tierName(tier: Int) = t("processed.tier_$tier")
    inline fun tierNameColored(tier: Int) = t("processed.tier_$tier.colored")

    inline fun ioStateName(stateName: String) = t("processed.io_state.$stateName")
    inline fun blockSide(sideName: String) = t("processed.side.$sideName")

    inline fun pipeLikeState(state: C) = t("processed.pipe_like_state", state)
    inline fun pipeLikeStateDisconnected() = t("processed.pipe_like_state.disconnected")
    inline fun pipeLikeStateConnected() = t("processed.pipe_like_state.connected")
    inline fun pipeLikeStateSplit() = t("processed.pipe_like_state.disallowed")

    inline fun ioButtonMessage(name: C, state: IoState) = t("processed.io_button.message", name, state)
    inline fun ioButtonTooltip(state: IoState) = t("processed.io_button.tooltip", state)

    inline fun configuringItemsLabel() = t("processed.screen.block_config.items")
    inline fun configuringFluidsLabel() = t("processed.screen.block_config.fluids")

    inline fun energyBarTooltip(amount: C, max: C) = t("processed.gui.widget.energy_bar", amount, max)
    inline fun energyBarUnitMillion(amount: Int) = t("processed.gui.widget.energy_bar.million", amount)
    inline fun energyBarUnitThousand(amount: Int) = t("processed.gui.widget.energy_bar.thousand", amount)
    inline fun energyBarUnitOnes(amount: Int) = t("processed.gui.widget.energy_bar.normal", amount)
    val energy = IntUnit(1, ::energyBarUnitOnes, 1_000, ::energyBarUnitThousand, 1_000_000, ::energyBarUnitMillion)

    inline fun cableTierTooltip(tier: C) = t("block.processed.cable.tooltip", tier)
    inline fun itemPipeTooltip(transferSpeed: Int) = t("block.processed.item_pipe.tooltip", transferSpeed)

    inline fun tieredMachineInfo(maxPower: C, nameColored: C) =
        t("processed.tiered_machine_info", maxPower, nameColored)

    inline fun poweredFurnaceName(tier: ProcessedTier) = t("block.processed.powered_furnace", tier)
    inline fun bigSmelterName() = t("block.processed.big_smelter")
    inline fun launchControllerName() = t("block.processed.launch_controller")

    inline fun multiblockAssembled() = t("processed.multiblocks.state.assembled")
    inline fun multiblockBroken() = t("processed.multiblocks.state.broken")
    inline fun planetoidWidgetTooltip(planetoidName: String) =
        t("processed.gui.planetoid_widget.tooltip", t(planetoidName))

    inline fun planetoidDistance(distance: C) = t("processed.gui.planetoid.distance", distance)
    inline fun planetoidGravity(gravity: Float) = t("processed.gui.planetoid.gravity", gravity)
    inline fun planetoidSelectionScreenGoUp() = t("processed.gui.planetoid_selection_screen.go_up")

    inline fun locationSelectorChangeTooltip() = t("item.processed.location_selector.change_tooltip")
    inline fun locationSelectorUnbound() = t("item.processed.location_selector.unbound")
    inline fun locationSelectorBound(name: String) = t("item.processed.location_selector.bound", t(name))
    inline fun locationSelectorUnboundMessage() = t("item.processed.location_selector.unbound_message")
    inline fun locationSelectorUnbindHint() = t("item.processed.location_selector.unbind_hint")

    inline fun unitKilometers(amount: Long) = t("processed.unit.kilometers", amount)
    inline fun unitKilometersLong(amount: Long) = t("processed.unit.kilometers.long", amount)
    val km = LongUnit(1, ::unitKilometers)
    val kilometer = LongUnit(1, ::unitKilometersLong)

    inline fun unitMillibucket(amount: Int) = t("processed.unit.millibuckets", amount)
    inline fun unitBucket(amount: Int) = t("processed.unit.buckets", amount)
    val mb = IntUnit(1, ::unitMillibucket, 1_000, ::unitBucket)

    inline fun unitItems(items: Int) = t("processed.unit.items", items)
    inline fun unitStacks(stacks: Int) = t("processed.unit.stacks", stacks)
    inline fun unitStacksItems(stacks: Int, items: Int) = t("processed.unit.stacks_items", stacks, items)
    val items = ItemUnit()

    inline fun mass(mass: Int) = t("processed.miner_attribute.mass", mass)
    inline fun maxDistance(distance: Int) = t("processed.miner_attribute.max_distance", distance)
    inline fun tankCapacity(capacity: Int) = t("processed.miner_attribute.tankCapacity", capacity)
    inline fun density(density: Float) = t("processed.miner_attribute.density", density)
    inline fun specificImpulse(specificImpulse: Int) = t("processed.miner_attribute.specific_impulse", specificImpulse)
    inline fun thrust(thrustInNewton: Int) = t("processed.miner_attribute.thrust", thrustInNewton / 1000.toDouble())
    inline fun efficiency(efficiency: Int) = t("processed.miner_attribute.efficiency", efficiency)
    inline fun miningSpeed(blocksPerMin: Int) = t("processed.miner_attribute.mining_speed", blocksPerMin)
    inline fun miningFuel(litersPerBlock: Float) = t("processed.miner_attribute.mining_fuel", litersPerBlock)
    inline fun cargoCapacity(capacityMb: Int, capacity: Int) = t("processed.miner_attribute.cargo_capacity", capacityMb, items(capacity))
    inline fun itemYield(amount: Int) = t("processed.miner_attribute.item_yield", items(amount))
    inline fun requiredFuel(amount: Int) = t("processed.miner_attribute.required_miner_fuel", mb(amount))
    inline fun miningTime(secs: Long) = t("processed.miner_attribute.mining_time", duration(secs))

    inline fun assembledMinerItemComponents() = t("item.processed.assembled_mining_rocket.components")
    inline fun assembledMinerItemStats() = t("item.processed.assembled_mining_rocket.stats")

    inline fun duration(secs: Long): MC {
        // TODO: Make this configurable
        return C.literal(DurationFormatUtils.formatDuration(TimeUnit.SECONDS.toMillis(secs), "HH:mm:ss", true))
    }
}

class IntUnit(variants: List<Pair<Int, (Int) -> MC>>) : (Int) -> MC {
    val variants = variants.sortedWith { (i0, _), (i1, _) -> i0.compareTo(i1) }

    constructor(vararg variants: Pair<Int, (Int) -> MC>) : this(variants.toList())
    constructor(amount1: Int, f1: (Int) -> MC) : this(listOf(Pair(amount1, f1)))
    constructor(amount1: Int, f1: (Int) -> MC, amount2: Int, f2: (Int) -> MC) : this(
        listOf(
            Pair(amount1, f1), Pair(amount2, f2)
        )
    )

    constructor(amount1: Int, f1: (Int) -> MC, amount2: Int, f2: (Int) -> MC, amount3: Int, f3: (Int) -> MC) : this(
        listOf(Pair(amount1, f1), Pair(amount2, f2), Pair(amount3, f3))
    )

    fun translate(amount: Int): MC {
        var last = variants.first()
        for (elem in variants) if (elem.first > last.first && elem.first < amount) last = elem
        return last.second(amount / last.first)
    }

    override fun invoke(amount: Int) = translate(amount)
}

class LongUnit(variants: List<Pair<Long, (Long) -> MC>>) : (Long) -> MC {
    val variants = variants.sortedWith { (i0, _), (i1, _) -> i0.compareTo(i1) }

    constructor(vararg variants: Pair<Long, (Long) -> MC>) : this(variants.toList())
    constructor(amount1: Long, f1: (Long) -> MC) : this(listOf(Pair(amount1, f1)))
    constructor(amount1: Long, f1: (Long) -> MC, amount2: Long, f2: (Long) -> MC) : this(
        listOf(
            Pair(amount1, f1), Pair(amount2, f2)
        )
    )

    constructor(amount1: Long, f1: (Long) -> MC, amount2: Long, f2: (Long) -> MC, amount3: Long, f3: (Long) -> MC) : this(
        listOf(Pair(amount1, f1), Pair(amount2, f2), Pair(amount3, f3))
    )

    fun translate(amount: Long): MC {
        var last = variants.first()
        for (elem in variants) if (elem.first > last.first && elem.first < amount) last = elem
        return last.second(amount / last.first)
    }

    override fun invoke(amount: Long) = translate(amount)
}

class ItemUnit : (Int) -> MC {
    override fun invoke(amount: Int): MC = if (amount <= 64) Translations.unitItems(amount)
    else if (amount % 64 == 0) Translations.unitStacks(amount / 64)
    else Translations.unitStacksItems(amount / 64, amount % 64)
}

inline fun t(key: String): MC = C.translatable(key)
inline fun t(key: String, vararg args: Any): MC {
    val args = arrayOf(*args)
    for (i in 0..<args.size) {
        args[i] = when (val v = args[i]) {
            is C, String, Boolean, is Number -> v
            is TranslatableEnum -> v.translatedName
            is Material -> v.component
            is ProcessedTier -> v.name
            else -> throw IllegalStateException("Unknown translatable object: $v ${v.javaClass.name}")
        }
    }
    return C.translatable(key, *args)
}

