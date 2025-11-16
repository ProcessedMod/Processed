package redcrafter07.processed.items

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.PlanetoidSelectionScreen
import redcrafter07.processed.miner.Planetoid
import kotlin.jvm.optionals.getOrNull

class LocationSelectorItem : Item(Properties()) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (level.isClientSide) {
            if(player.isShiftKeyDown) {
                val stack = player.getItemInHand(usedHand)
                if(stack.isEmpty) return InteractionResultHolder.fail(stack)
                stack.remove(ModDataComponents.BOUND_PLANETOID)

                player.displayClientMessage(Translations.locationSelectorUnboundMessage(), true)
            }   else {
                val registry = level.registryAccess().registry(Planetoid.REGISTRY_KEY).getOrNull()
                if (registry == null) ProcessedMod.LOG.warn("Failed to get the planetoid registry")
                else Minecraft.getInstance().setScreen(PlanetoidSelectionScreen.fromRegistrySun(registry, usedHand))
            }
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide)
    }

    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
        val boundPlanetoid = stack.get(ModDataComponents.BOUND_PLANETOID)
        if (boundPlanetoid == null) {
            tooltip.add(Component.empty())
            tooltip.add(Translations.locationSelectorChangeTooltip())
            tooltip.add(Component.empty())
            tooltip.add(Translations.locationSelectorUnbound().withStyle(ChatFormatting.RED))
        } else {
            tooltip.add(Component.empty())
            tooltip.add(Translations.locationSelectorUnbindHint())
            tooltip.add(Component.empty())
            val planetoid = context.level()?.registryAccess()?.registry(Planetoid.REGISTRY_KEY)?.getOrNull()
                ?.get(boundPlanetoid.location)
            tooltip.add(
                Translations.locationSelectorBound(planetoid?.name ?: boundPlanetoid.translatedName)
                    .withStyle(ChatFormatting.BLUE)
            )
            if (planetoid != null) {
                if (planetoid.distance.isPresent) {
                    val distance = Translations.unitKilometers(planetoid.distance.get())
                    val comp = Translations.planetoidDistance(distance).withStyle(ChatFormatting.DARK_GRAY)
                    tooltip.add(Component.literal("  ").append(comp))
                }
                if (planetoid.gravity.isPresent) {
                    val gravity =
                        Translations.planetoidGravity(planetoid.gravity.get()).withStyle(ChatFormatting.DARK_GRAY)
                    tooltip.add(Component.literal("  ").append(gravity))
                }

            }
        }
        tooltip.add(Component.empty())
        super.appendHoverText(stack, context, tooltip, tooltipFlag)
    }

    data class BoundPlanetoid(val location: ResourceLocation, val translatedName: String) {
        companion object {
            val CODEC: Codec<BoundPlanetoid> = RecordCodecBuilder.create {
                it.group(
                    ResourceLocation.CODEC.fieldOf("location").forGetter(BoundPlanetoid::location),
                    Codec.STRING.fieldOf("name").forGetter(BoundPlanetoid::translatedName),
                ).apply(it, ::BoundPlanetoid)
            }
            val STREAM_CODEC: StreamCodec<ByteBuf, BoundPlanetoid> = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                BoundPlanetoid::location,
                ByteBufCodecs.STRING_UTF8,
                BoundPlanetoid::translatedName,
                ::BoundPlanetoid
            )
        }
    }
}