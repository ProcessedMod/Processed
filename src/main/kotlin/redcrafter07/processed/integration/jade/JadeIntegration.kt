package redcrafter07.processed.integration.jade

import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.widgets.EnergyBarWidget
import redcrafter07.processed.multiblock.MultiblockBlock
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.rl
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig

@WailaPlugin
class JadeIntegration : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(ProcessedEnergyServerProvider, Block::class.java)
        registration.registerBlockDataProvider(MultiblockAssembledStateServerProvider, MultiblockBlock::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(ProcessedEnergyProvider, Block::class.java)
        registration.registerBlockComponent(MultiblockAssembledStateProvider, MultiblockBlock::class.java)
    }

    object MultiblockAssembledStateProvider : IBlockComponentProvider {
        override fun appendTooltip(
            tooltip: ITooltip, accessor: BlockAccessor, cfg: IPluginConfig
        ) {
            val state = MultiblockAssembledStateServerProvider.decodeFromData(accessor)
            if(state.isPresent) tooltip.add(state.get())
        }

        override fun getUid(): ResourceLocation = rl("multiblock_state")

    }

    object ProcessedEnergyProvider : IBlockComponentProvider {
        override fun appendTooltip(
            tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig
        ) {
            val energy = ProcessedEnergyServerProvider.decodeFromData(accessor)
            if (energy.isPresent) {
                val energy = energy.get()
                val showOnes = accessor.player.isShiftKeyDown
                val amount = EnergyBarWidget.getEnergyComponent(energy.energy, showOnes)
                val maxEnergy = EnergyBarWidget.getEnergyComponent(energy.maxEnergy, showOnes)
                val text = Component.literal(" ").append(amount).append(" / ").append(maxEnergy)
                val progress = energy.energy.toFloat() / energy.maxEnergy.toFloat()
                tooltip.add(EnergyElement(text, progress))
            }
        }

        override fun getUid(): ResourceLocation = rl("energy")
    }

    object ProcessedEnergyServerProvider : StreamServerDataProvider<BlockAccessor, EnergyData> {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EnergyData> = StreamCodec.composite(
            ByteBufCodecs.INT, EnergyData::energy, ByteBufCodecs.INT, EnergyData::maxEnergy, ::EnergyData
        ).cast()

        override fun streamData(accessor: BlockAccessor): EnergyData? {
            val be = accessor.level.getCapability(ProcessedPower.BLOCK, accessor.position, null) ?: return null
            return EnergyData(be.energy().energyStored, be.energy().maxEnergyStored)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, EnergyData> = STREAM_CODEC

        override fun getUid(): ResourceLocation = rl("energy")
    }

    object MultiblockAssembledStateServerProvider : StreamServerDataProvider<BlockAccessor, Component> {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Component> = ComponentSerialization.STREAM_CODEC

        override fun streamData(accessor: BlockAccessor): Component? {
            val be = accessor.level.getBlockEntity(accessor.position)
            if (be !is MultiblockBlockEntity) return null
            if (!be.isAssembled) return Translations.multiblockBroken().withStyle(ChatFormatting.RED)
            else {
                val assembled = Translations.multiblockAssembled().withStyle(ChatFormatting.GREEN)
                val state = be.state() ?: return assembled
                return Component.empty().append(assembled).append(" (").append(state).append(")")
            }
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, Component> = STREAM_CODEC

        override fun getUid(): ResourceLocation = rl("multiblock_state")
    }

    data class EnergyData(val energy: Int, val maxEnergy: Int)
}