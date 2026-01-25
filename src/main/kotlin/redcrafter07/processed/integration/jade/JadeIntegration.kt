package redcrafter07.processed.integration.jade

import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec2
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.Translations
import redcrafter07.processed.block.TieredRecipeBlock
import redcrafter07.processed.block.tile_entities.TieredRecipeBlockEntity
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.widgets.EnergyBarWidget
import redcrafter07.processed.multiblock.MultiblockBlock
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.rl
import snownee.jade.api.*
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.BoxStyle
import snownee.jade.api.ui.IElementHelper
import snownee.jade.overlay.DisplayHelper
import kotlin.math.max

@WailaPlugin
class JadeIntegration : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(ProcessedEnergyServerProvider, Block::class.java)
        registration.registerBlockDataProvider(MultiblockAssembledStateServerProvider, MultiblockBlock::class.java)
        registration.registerBlockDataProvider(MultiblockCraftingStateServerProvider, TieredRecipeBlock::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(ProcessedEnergyProvider, Block::class.java)
        registration.registerBlockComponent(MultiblockAssembledStateProvider, MultiblockBlock::class.java)
        registration.registerBlockComponent(MultiblockCraftingStateProvider, TieredRecipeBlock::class.java)
    }

    object MultiblockAssembledStateProvider : IBlockComponentProvider {
        override fun appendTooltip(
            tooltip: ITooltip, accessor: BlockAccessor, cfg: IPluginConfig
        ) {
            val state = MultiblockAssembledStateServerProvider.decodeFromData(accessor)
            if (state.isPresent) tooltip.add(state.get().copy().withStyle(ChatFormatting.LIGHT_PURPLE))
        }

        override fun getUid() = rl("multiblock_state")

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

        override fun getUid() = rl("energy")
    }

    object ProcessedEnergyServerProvider : StreamServerDataProvider<BlockAccessor, EnergyData> {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EnergyData> = StreamCodec.composite(
            ByteBufCodecs.INT, EnergyData::energy, ByteBufCodecs.INT, EnergyData::maxEnergy, ::EnergyData
        ).cast()

        override fun streamData(accessor: BlockAccessor): EnergyData? {
            val be = accessor.level.getCapability(ProcessedPower.BLOCK, accessor.position, null) ?: return null
            return EnergyData(be.energy().energyStored, be.energy().maxEnergyStored)
        }

        override fun streamCodec() = STREAM_CODEC
        override fun getUid() = rl("energy")
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

        override fun streamCodec() = STREAM_CODEC
        override fun getUid() = rl("multiblock_state")
    }

    object MultiblockCraftingStateServerProvider : StreamServerDataProvider<BlockAccessor, RecipeData> {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RecipeData> = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC,
            RecipeData::items,
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
            RecipeData::fluids,
            ByteBufCodecs.INT,
            RecipeData::ticksRemaining,
            ByteBufCodecs.FLOAT,
            RecipeData::progress,
            ::RecipeData
        )

        override fun streamData(accessor: BlockAccessor): RecipeData? {
            val be = accessor.level.getBlockEntity(accessor.position)
            if (be !is TieredRecipeBlockEntity) return null
            val recipe = be.recipeData ?: return null
            val speed = be.tier.speedMultiplier
            val ticksRemaining = max(0, (recipe.maxProgress - recipe.progress) / speed)

            return RecipeData(
                recipe.outputItems, recipe.outputLiquids, ticksRemaining, recipe.progress.toFloat() / recipe.maxProgress
            )
        }

        override fun streamCodec() = STREAM_CODEC
        override fun getUid() = rl("crafting_state")
    }

    data class RecipeData(
        val items: List<ItemStack>, val fluids: List<FluidStack>, val ticksRemaining: Int, val progress: Float
    )

    object MultiblockCraftingStateProvider : IBlockComponentProvider {
        override fun appendTooltip(
            tooltip: ITooltip, accessor: BlockAccessor, cfg: IPluginConfig
        ) {
            val state = MultiblockCraftingStateServerProvider.decodeFromData(accessor)
            if (state.isPresent) {
                val state = state.get()
                val helper = IElementHelper.get()
                val text = Translations.craftingDuration(state.ticksRemaining)
                tooltip.add(ProgressElement(text, state.progress))
                if (state.fluids.isEmpty() && state.items.isEmpty()) return
                tooltip.add(Translations.jadeCraftingOutput())
                for (item in state.items) {
                    tooltip.add(helper.smallItem(item))
                    tooltip.append(helper.spacer(2, 0))
                    tooltip.append(Component.literal("${item.count}x ").append(item.hoverName))
                }
                for (fluid in state.fluids) {
                    val fluidObject = JadeFluidObject.of(fluid.fluid, fluid.amount.toLong(), fluid.componentsPatch)
                    val lineHeight = DisplayHelper.font().lineHeight
                    val widget =
                        helper.fluid(fluidObject).size(Vec2((lineHeight - 1).toFloat(), (lineHeight - 1).toFloat()))
                    tooltip.add(widget)
                    tooltip.append(helper.spacer(2, 0))
                    tooltip.append(Translations.fluidWidgetTooltip(fluid.hoverName, fluid.amount))
                }
            }
        }

        override fun getUid() = rl("crafting_state")

    }

    data class EnergyData(val energy: Int, val maxEnergy: Int)
}