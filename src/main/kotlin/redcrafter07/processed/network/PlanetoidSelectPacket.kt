package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.items.LocationSelectorItem
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.rl

class PlanetoidSelectPacket(val planetoidKey: ResourceLocation, val translatedName: String, val hand: InteractionHand) :
    CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<PlanetoidSelectPacket> = CustomPacketPayload.Type(rl("select_planetoid"))
        val CODEC: StreamCodec<ByteBuf, PlanetoidSelectPacket> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            PlanetoidSelectPacket::planetoidKey,
            ByteBufCodecs.STRING_UTF8,
            PlanetoidSelectPacket::translatedName,
            ByteBufCodecs.BOOL.map(
                { if (it) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND },
                { it == InteractionHand.MAIN_HAND }),
            PlanetoidSelectPacket::hand,
            ::PlanetoidSelectPacket
        )
    }

    fun handleServer(context: IPayloadContext) {
        val item = context.player().getItemInHand(hand)
        if(item.isEmpty) return
        item.set(ModDataComponents.BOUND_PLANETOID, LocationSelectorItem.BoundPlanetoid(planetoidKey, translatedName))
    }

    override fun type(): CustomPacketPayload.Type<PlanetoidSelectPacket> = TYPE
}