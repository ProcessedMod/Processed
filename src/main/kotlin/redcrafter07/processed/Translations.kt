@file:Suppress("NOTHING_TO_INLINE")

package redcrafter07.processed

import net.neoforged.neoforge.common.TranslatableEnum
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.items.WrenchMode
import redcrafter07.processed.materials.Material
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

    inline fun cableState(state: C) = t("processed.cable_state", state)
    inline fun cableStateDisconnected() = t("processed.cable_state.disconnected")
    inline fun cableStateConnected() = t("processed.cable_state.connected")
    inline fun cableStateSplit() = t("processed.cable_state.disallowed")

    inline fun ioButtonMessage(name: C, state: IoState) = t("processed.io_button.message", name, state)
    inline fun ioButtonTooltip(state: IoState) = t("processed.io_button.tooltip", state)

    inline fun configuringItemsLabel() = t("processed.screen.block_config.items")
    inline fun configuringFluidsLabel() = t("processed.screen.block_config.fluids")

    inline fun energyBarUnitMillion(amount: Int) = t("processed.gui.widget.energy_bar.million", amount)
    inline fun energyBarUnitThousand(amount: Int) = t("processed.gui.widget.energy_bar.thousand", amount)
    inline fun energyBarUnitOnes(amount: Int) = t("processed.gui.widget.energy_bar.normal", amount)
    inline fun energyBarTooltip(amount: C, max: C) = t("processed.gui.widget.energy_bar", amount, max)

    inline fun cableTransferSpeedTooltip(tier: C) = t("block.processed.cable.tooltip", tier)

    inline fun tieredMachineInfo(maxPower: C, nameColored: C) = t("processed.tiered_machine_info", maxPower, nameColored)
    inline fun poweredFurnaceName(tier: ProcessedTier) = t("block.processed.powered_furnace", tier)
    inline fun bigSmelterName() = t("block.processed.big_smelter")
}

inline fun t(key: String): MC = C.translatable(key)
inline fun t(key: String, vararg args: Any): MC {
    val args = arrayOf(*args)
    for (i in 0..<args.size) {
        args[i] = when(val v = args[i]) {
            is C, String, Boolean, is Number -> v
            is TranslatableEnum -> v.translatedName
            is Material -> v.component
            is ProcessedTier -> v.name
            else -> throw IllegalStateException("Unknown translatable object: $v ${v.javaClass.name}")
        }
    }
    return C.translatable(key, *args)
}

