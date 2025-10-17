package redcrafter07.processed.items

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.ProcessedTier

class MinerItem(val tier: ProcessedTier) : Item(Properties().durability(100)) {
    override fun getName(stack: ItemStack): Component = tier.name.append(" Miner")

    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag
    ) {
        val comp = stack.components.get(ModDataComponents.MINER_DATA.get())
        if (comp == null) tooltip.add(Component.literal("Unformed").withStyle(ChatFormatting.RED))
        else tooltip.add(Component.literal("Engine: ").append(comp.engineTier.nameColored))

        super.appendHoverText(stack, context, tooltip, flag)
    }

    companion object {
        val CODEC: Codec<MinerData> = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("engineRL").forGetter(MinerData::engineRL),
                ProcessedTier.CODEC.fieldOf("engineTier").forGetter(MinerData::engineTier),
            ).apply(it, ::MinerData)
        }
        val STREAM_CODEC: StreamCodec<ByteBuf, MinerData> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            MinerData::engineRL,
            ProcessedTier.STREAM_CODEC,
            MinerData::engineTier,
            ::MinerData
        )

        data class MinerData(val engineRL: ResourceLocation, val engineTier: ProcessedTier)
    }
}