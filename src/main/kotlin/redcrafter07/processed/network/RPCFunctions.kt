package redcrafter07.processed.network

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.RegisterEvent
import redcrafter07.processed.items.LocationSelectorItem
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rpc.RpcRegistry

object RPCFunctions {
    val selectPlanetoid =
        RpcRegistry.registerServer("select_planetoid") { sender, planetoid: ResourceLocation, translatedName: String, hand: InteractionHand ->
            val item = sender.player.getItemInHand(hand)
            if (item.`is`(ModItems.LOCATION_SELECTOR)) item.set(
                ModDataComponents.BOUND_PLANETOID, LocationSelectorItem.BoundPlanetoid(planetoid, translatedName)
            )
        }

    fun register(bus: IEventBus) {
        // no-op that runs at init. Should probably do smth like DeferredRegistry but idc
        bus.addListener<RegisterEvent> {}
    }
}