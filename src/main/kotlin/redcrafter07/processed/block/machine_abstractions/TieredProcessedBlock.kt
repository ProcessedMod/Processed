package redcrafter07.processed.block.machine_abstractions

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.TieredBlock
import redcrafter07.processed.gui.widgets.EnergyBarWidget
import redcrafter07.processed.materials.MaterialContainer
import redcrafter07.processed.materials.data.MaterialBase

open class TieredProcessedBlock(
    properties: Properties,
    val baseName: String,
    override val tier: ProcessedTier,
    val blockEntity: BlockEntityType.BlockEntitySupplier<TieredProcessedMachine>,
) : ProcessedBlock(properties), MaterialContainer, TieredBlock {
    override fun getName(): MutableComponent = Component.translatable(baseName, tier.name)

    override fun getDescription(
        tooltips: MutableList<Component>, flag: TooltipFlag
    ) {
        tooltips.add(Component.translatable("$baseName.tooltip"))
        tooltips.add(getMachineInfo(tier, flag.hasShiftDown()))
    }

    override val material: MaterialBase get() = tier.material

    private fun getMachineInfo(tier: ProcessedTier, shift: Boolean): MutableComponent {
        val maxPower =
            if (shift) Translations.energyBarUnitOnes(tier.maxPower) else EnergyBarWidget.getEnergyComponent(tier.maxPower)
        return Translations.tieredMachineInfo(maxPower.withStyle(ChatFormatting.GREEN), tier.nameColored)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        val entity = blockEntity.create(pos, state)
        entity.tier = tier
        return entity
    }
}