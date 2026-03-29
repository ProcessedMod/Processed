@file:Suppress("NOTHING_TO_INLINE")

package redcrafter07.processed

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.TranslatableEnum
import org.apache.commons.lang3.time.DurationFormatUtils
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.items.WrenchMode
import redcrafter07.processed.materials.data.MaterialBase
import java.util.concurrent.TimeUnit
import kotlin.math.truncate
import net.minecraft.network.chat.Component as C
import net.minecraft.network.chat.MutableComponent as MC

object Translations {
    inline fun materialDust(material: MaterialBase) = t("processed.item.material_dust", material)
    inline fun materialSmallDust(material: MaterialBase) = t("processed.item.material_dust.small", material)
    inline fun materialImpureDust(material: MaterialBase) = t("processed.item.material_dust.impure", material)
    inline fun materialWashedDust(material: MaterialBase) = t("processed.item.material_dust.washed", material)
    inline fun materialPureDust(material: MaterialBase) = t("processed.item.material_dust.pure", material)
    inline fun materialIngot(material: MaterialBase) = t("processed.item.material_ingot", material)
    inline fun materialNugget(material: MaterialBase) = t("processed.item.material_nugget", material)
    inline fun materialRaw(material: MaterialBase) = t("processed.item.material_raw", material)
    inline fun materialOre(material: MaterialBase) = t("processed.item.material_ore", material)
    inline fun materialCable(material: MaterialBase) = t("processed.item.material_cable", material)
    inline fun materialTransporter(material: MaterialBase) = t("processed.item.material_transporter", material)
    inline fun materialPipe(material: MaterialBase) = t("processed.item.material_pipe", material)
    inline fun materialName(identifier: String) = t("processed.material.$identifier")
    inline fun blockItemTooltip(id: String): MC = t("block.processed.$id.tooltip").withStyle(ChatFormatting.GRAY)
    inline fun itemTooltip(id: String) = t("item.processed.$id.tooltip")
    inline fun wrenchMode(modeName: String) = t("item.processed.wrench.mode.$modeName")
    inline fun wrenchModeTooltip(mode: WrenchMode) = t("item.processed.wrench.mode", mode)
    inline fun itemFluidCover() = t("item.processed.fluid_cover")

    inline fun mainItemGroup() = t("item_group.processed.main")
    inline fun materialsItemGroup() = t("item_group.processed.materials")

    inline fun tierName(tier: Int) = t("processed.tier_$tier")

    inline fun ioStateName(stateName: String) = t("processed.io_state.$stateName")
    inline fun blockSide(sideName: String) = t("processed.side.$sideName")

    inline fun pipeLikeState(state: C) = t("processed.transmitter_state", state)
    inline fun pipeLikeStateDisconnected() = t("processed.transmitter_state.disconnected")
    inline fun pipeLikeStateConnected() = t("processed.transmitter_state.connected")
    inline fun pipeLikeStateSplit() = t("processed.transmitter_state.disallowed")

    inline fun ioButtonMessage(name: C, state: IoState) = t("processed.io_button.message", name, state)
    inline fun ioButtonTooltip(state: IoState) = t("processed.io_button.tooltip", state)

    inline fun configuringItemsLabel() = t("processed.screen.block_config.items")
    inline fun configuringFluidsLabel() = t("processed.screen.block_config.fluids")

    inline fun energyBarTooltip(amount: C, max: C) = t("processed.gui.widget.energy_bar", amount, max)
    inline fun energyBarUnitMillion(amount: Number) = t("processed.gui.widget.energy_bar.million", amount)
    inline fun energyBarUnitThousand(amount: Number) = t("processed.gui.widget.energy_bar.thousand", amount)
    inline fun energyBarUnitOnes(amount: Number) = t("processed.gui.widget.energy_bar.normal", amount)
    val energy = IntUnit(1, ::energyBarUnitOnes, 1_000, ::energyBarUnitThousand, 1_000_000, ::energyBarUnitMillion)

    inline fun cableTierTooltip(tier: C, transferSpeed: Int) =
        t("block.processed.cable.tooltip", tier, energy(transferSpeed))

    inline fun transporterTooltip(transferSpeed: Int) = t("block.processed.transporter.tooltip", items(transferSpeed))
    inline fun pipeTooltip(transferSpeed: Int) = t("block.processed.pipe.tooltip", mb(transferSpeed))

    inline fun tieredMachineInfo(maxPower: C, nameColored: C) =
        t("processed.tiered_machine_info", maxPower, nameColored)

    inline fun poweredFurnaceName(tier: ProcessedTier) = t("block.processed.powered_furnace", tier)
    inline fun sifterName(tier: ProcessedTier) = t("block.processed.sifter", tier)
    inline fun crusherName(tier: ProcessedTier) = t("block.processed.crusher", tier)
    inline fun washerName(tier: ProcessedTier) = t("block.processed.washer", tier)
    inline fun purifierName(tier: ProcessedTier) = t("block.processed.purifier", tier)
    inline fun energyHatchName(tier: ProcessedTier) = t("block.processed.energy_hatch", tier)
    inline fun energyHatchTooltip(amount: Int) =
        t("block.processed.energy_hatch.tooltip", energy.translate(amount).withStyle(ChatFormatting.GREEN))
    inline fun computationHatchName(tier: ProcessedTier) = t("block.processed.computation_hatch", tier)
    inline fun computationHatchTooltip(amount: Long) =
        t("block.processed.computation_hatch.tooltip", hashes.translate(amount).withStyle(ChatFormatting.GREEN))

    inline fun bigSmelterName() = t("block.processed.big_smelter")
    inline fun launchControllerName() = t("block.processed.launch_controller")

    inline fun launchControllerStateIdle() = t("block.processed.launch_controller.state.idle")
    inline fun launchControllerStateMining(secondsRemaining: Long) =
        t("block.processed.launch_controller.state.mining", duration(secondsRemaining))

    inline fun launchControllerStateTravelling(secondsRemaining: Long) =
        t("block.processed.launch_controller.state.travelling", duration(secondsRemaining))

    inline fun launchControllerStateTravellingBack(secondsRemaining: Long) =
        t("block.processed.launch_controller.state.travelling_back", duration(secondsRemaining))

    inline fun multiblockAssembled() = t("processed.multiblocks.state.assembled")
    inline fun multiblockBroken() = t("processed.multiblocks.state.broken")
    inline fun planetoidWidgetTooltip(planetoidName: String) =
        t("processed.gui.planetoid_widget.tooltip", t(planetoidName))

    inline fun planetoidDistance(distance: C) = t("processed.gui.planetoid.distance", distance)
    inline fun planetoidGravity(gravity: Float) = t("processed.gui.planetoid.gravity", gravity)
    inline fun planetoidResource(resource: ResourceLocation) = t("processed.gui.planetoid.resource", itemName(resource))
    inline fun planetoidSelectionScreenGoUp() = t("processed.gui.planetoid_selection_screen.go_up")
    inline fun itemName(rl: ResourceLocation): C = BuiltInRegistries.ITEM.get(rl).description
    inline fun bucketName(fluid: C) = t("item.processed.bucket", fluid)

    inline fun locationSelectorChangeTooltip() = t("item.processed.location_selector.change_tooltip")
    inline fun locationSelectorUnbound() = t("item.processed.location_selector.unbound")
    inline fun locationSelectorBound(name: String) = t("item.processed.location_selector.bound", t(name))
    inline fun locationSelectorUnboundMessage() = t("item.processed.location_selector.unbound_message")
    inline fun locationSelectorUnbindHint() = t("item.processed.location_selector.unbind_hint")

    inline fun unitKilometers(amount: Number) = t("processed.unit.kilometers", amount)
    inline fun unitKilometersLong(amount: Number) = t("processed.unit.kilometers.long", amount)
    val km = LongUnit(1, ::unitKilometers)
    val kilometer = LongUnit(1, ::unitKilometersLong)

    inline fun unitMillibucket(amount: Number) = t("processed.unit.millibuckets", amount)
    inline fun unitBucket(amount: Number) = t("processed.unit.buckets", amount)
    val mb = IntUnit(1, ::unitMillibucket, 1_000, ::unitBucket)

    inline fun unitItems(items: Int) = t("processed.unit.items", items)
    inline fun unitStacks(stacks: Int) = t("processed.unit.stacks", stacks)
    inline fun unitStacksItems(stacks: Int, items: Int) = t("processed.unit.stacks_items", stacks, items)
    val items = ItemUnit()

    inline fun hashes1024P0(amount: Number) = t("processed.unit.hashes.1024_0", amount)
    inline fun hashes1024P1(amount: Number) = t("processed.unit.hashes.1024_1", amount)
    inline fun hashes1024P2(amount: Number) = t("processed.unit.hashes.1024_2", amount)
    inline fun hashes1024P3(amount: Number) = t("processed.unit.hashes.1024_3", amount)
    inline fun hashes1024P4(amount: Number) = t("processed.unit.hashes.1024_4", amount)
    inline fun hashes1024P5(amount: Number) = t("processed.unit.hashes.1024_5", amount)
    val hashes = LongUnit(
        0x1L, ::hashes1024P0,
        0x400L, ::hashes1024P1,
        0x100000L, ::hashes1024P2,
        0x40000000L, ::hashes1024P3,
        0x10000000000L, ::hashes1024P4,
        0x4000000000000L, ::hashes1024P5,
    )

    inline fun mass(mass: Int) = t("processed.miner_attribute.mass", mass)
    inline fun maxDistance(distance: Int) = t("processed.miner_attribute.max_distance", distance)
    inline fun tankCapacity(capacity: Int) = t("processed.miner_attribute.tankCapacity", capacity)
    inline fun density(density: Float) = t("processed.miner_attribute.density", density)
    inline fun specificImpulse(specificImpulse: Int) = t("processed.miner_attribute.specific_impulse", specificImpulse)
    inline fun thrust(thrustInNewton: Int) = t("processed.miner_attribute.thrust", thrustInNewton / 1000.toDouble())
    inline fun efficiency(efficiency: Int) = t("processed.miner_attribute.efficiency", efficiency)
    inline fun miningSpeed(blocksPerMin: Int) = t("processed.miner_attribute.mining_speed", blocksPerMin)
    inline fun storedFuel(amount: Int) = t("processed.miner_attribute.stored_fuel", mb(amount))

    inline fun fuel(fuel: C) = t("processed.miner_attribute.fuel", fuel)
    inline fun cargoCapacity(capacity: Int) = t("processed.miner_attribute.cargo_capacity", items(capacity))

    inline fun itemYield(amount: Int) = t("processed.miner_attribute.item_yield", items(amount))
    inline fun miningTime(secs: Long) = t("processed.miner_attribute.mining_time", duration(secs))

    inline fun assembledMinerItemComponents() = t("item.processed.assembled_mining_rocket.components")
    inline fun assembledMinerItemStats() = t("item.processed.assembled_mining_rocket.stats")
    inline fun fluidWidgetTooltip(fluid: C, amount: Int) = t("processed.gui.widget.fluid", fluid, mb(amount))
    inline fun fluidWidgetTooltip(fluid: C, amount: Int, capacity: Int) =
        t("processed.gui.widget.fluid.capacity", fluid, mb(amount), mb(capacity))

    inline fun launchControllerScreenResult(item: C) = t("processed.gui.launch_controller_screen.result", item)
    inline fun launchControllerScreenResultAmount(amount: Int) =
        t("processed.gui.launch_controller_screen.result_amount", items(amount))

    inline fun launchControllerScreenMining(secondsRemaining: Long) =
        t("processed.gui.launch_controller_screen.mining", duration(secondsRemaining))

    inline fun launchControllerScreenTravelling(secondsRemaining: Long) =
        t("processed.gui.launch_controller_screen.travelling", duration(secondsRemaining))

    inline fun launchControllerScreenTravellingBack(secondsRemaining: Long) =
        t("processed.gui.launch_controller_screen.travelling_back", duration(secondsRemaining))

    inline fun launchControllerScreenWaiting() = t("processed.gui.launch_controller_screen.waiting")
    inline fun launchControllerScreenStored() = t("processed.gui.launch_controller_screen.stored")
    inline fun launchControllerScreenStoredInfinity() = t("processed.gui.launch_controller_screen.stored.infinity")
    inline fun launchControllerScreenFuel(amount: Int, max: Int) =
        t("processed.gui.launch_controller_screen.fuel", mb(amount), mb(max))

    inline fun launchControllerScreenDestination(name: C) =
        t("processed.gui.launch_controller_screen.destination", name)

    inline fun launchControllerScreenDuration(seconds: Long) =
        t("processed.gui.launch_controller_screen.duration", duration(seconds))

    inline fun buttonPushPullPush() = t("processed.gui.button_push_pull.push")
    inline fun buttonPushPullPull() = t("processed.gui.button_push_pull.pull")

    inline fun jadeCraftingOutput() = t("config.jade.plugin_processed.crafting_state.output")

    inline fun itemContainedResearch(contained: C) = t("item.processed.component.research", contained)

    inline fun craftingDurationTicks(n: Number) = t("processed.unit.crafting_duration.ticks", n)
    inline fun craftingDurationSecs(n: Number) = t("processed.unit.crafting_duration.secs", n)
    inline fun craftingDurationMins(n: Number) = t("processed.unit.crafting_duration.mins", n)
    val craftingDuration = IntUnit(
        1, ::craftingDurationTicks, 20, ::craftingDurationSecs, 1200, ::craftingDurationMins
    )

    inline fun duration(secs: Long): MC {
        // TODO: Make this configurable
        return C.literal(DurationFormatUtils.formatDuration(TimeUnit.SECONDS.toMillis(secs), "HH:mm:ss", true))
    }

    inline fun guiProgressTooltip(durationTicks: Int) =
        t("processed.gui.progress_tooltip", craftingDuration(durationTicks))
}

class IntUnit(variants: List<Pair<Int, (Number) -> MC>>) : (Int) -> MC {
    val variants = variants.sortedWith { (i0, _), (i1, _) -> i0.compareTo(i1) }

    constructor(vararg variants: Pair<Int, (Number) -> MC>) : this(variants.toList())
    constructor(amount1: Int, f1: (Number) -> MC) : this(listOf(Pair(amount1, f1)))
    constructor(amount1: Int, f1: (Number) -> MC, amount2: Int, f2: (Number) -> MC) : this(
        listOf(
            Pair(amount1, f1), Pair(amount2, f2)
        )
    )

    constructor(
        amount1: Int, f1: (Number) -> MC, amount2: Int, f2: (Number) -> MC, amount3: Int, f3: (Number) -> MC
    ) : this(
        listOf(Pair(amount1, f1), Pair(amount2, f2), Pair(amount3, f3))
    )

    fun translate(amount: Int): MC {
        var last = variants.first()
        for (elem in variants) if (elem.first > last.first && elem.first < amount) last = elem
        val amount = truncate((amount.toFloat() / last.first.toFloat()) * 100f) / 100f
        return if (amount - amount.toInt().toFloat() < 0.01) last.second(amount.toInt())
        else last.second(amount)
    }

    override fun invoke(amount: Int) = translate(amount)
}

class LongUnit(variants: List<Pair<Long, (Number) -> MC>>) : (Long) -> MC {
    val variants = variants.sortedWith { (i0, _), (i1, _) -> i0.compareTo(i1) }

    constructor(vararg variants: Pair<Long, (Number) -> MC>) : this(variants.toList())
    constructor(amount1: Long, f1: (Number) -> MC) : this(listOf(Pair(amount1, f1)))
    constructor(amount1: Long, f1: (Number) -> MC, amount2: Long, f2: (Number) -> MC) : this(
        listOf(
            Pair(amount1, f1), Pair(amount2, f2)
        )
    )

    constructor(
        amount1: Long, f1: (Number) -> MC, amount2: Long, f2: (Number) -> MC, amount3: Long, f3: (Number) -> MC
    ) : this(
        listOf(Pair(amount1, f1), Pair(amount2, f2), Pair(amount3, f3))
    )

    constructor(
        amount1: Long, f1: (Number) -> MC, amount2: Long, f2: (Number) -> MC, amount3: Long, f3: (Number) -> MC,
        amount4: Long, f4: (Number) -> MC, amount5: Long, f5: (Number) -> MC, amount6: Long, f6: (Number) -> MC,
    ) : this(
        listOf(
            Pair(amount1, f1),
            Pair(amount2, f2),
            Pair(amount3, f3),
            Pair(amount4, f4),
            Pair(amount5, f5),
            Pair(amount6, f6)
        )
    )

    fun translate(amount: Long): MC {
        var last = variants.first()
        for (elem in variants) if (elem.first > last.first && elem.first < amount) last = elem

        val amount = truncate((amount.toDouble() / last.first.toDouble()) * 100.0) / 100.0
        return if (amount - amount.toLong().toDouble() < 0.01) last.second(amount.toLong())
        else last.second(amount)
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
            is MaterialBase -> v.component
            is ProcessedTier -> v.name
            else -> throw IllegalStateException("Unknown translatable object: $v ${v.javaClass.name}")
        }
    }
    return C.translatable(key, *args)
}