package redcrafter07.processed.covers.covers

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.covers.CoverBehavior
import redcrafter07.processed.covers.CoverMenu
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.gui.sync.MenuSynced
import redcrafter07.processed.rpc.RPCMethod

class FluidCoverMenu(
    containerId: Int, playerInventory: Inventory, cover: CoverBehavior?, pos: BlockPos, dir: Direction
) : CoverMenu(ModMenuTypes.FLUID_COVER_MENU.get(), containerId, playerInventory, cover, pos, dir) {
    constructor(containerId: Int, inventory: Inventory, buf: RegistryFriendlyByteBuf?) : this(
        containerId,
        inventory,
        null,
        (buf ?: throw NullPointerException("Tried opening a menu on the client without a buffer")).readBlockPos(),
        Direction.STREAM_CODEC.decode(buf)
    )

    @MenuSynced(onSynchronised = "onPushChange")
    var pushing: Boolean = false
        get() = (cover as? FluidCover.Behavior)?.pushing ?: field

    fun onPushChange(ignoredOld: Boolean) {
        val mc = Minecraft.getInstance().screen
        if(mc is FluidCoverScreen) mc.onPushChange()
//        (Minecraft.getInstance().screen as? FluidCoverScreen)?.onPushChange()
    }

    @RPCMethod
    fun togglePushing() {
        val cover = cover as? FluidCover.Behavior ?: return
        cover.pushing = !cover.pushing
    }
}