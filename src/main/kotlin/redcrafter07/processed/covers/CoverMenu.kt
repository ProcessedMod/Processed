package redcrafter07.processed.covers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import redcrafter07.processed.Attachments
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

abstract class CoverMenu(
    menuType: MenuType<*>,
    containerId: Int,
    playerInventory: Inventory,
    val cover: CoverBehavior?,
    val pos: BlockPos,
    val dir: Direction
) : ProcessedContainerMenu(menuType, containerId, playerInventory) {

    override fun stillValid(player: Player): Boolean {
        if (player.distanceToSqr(pos.toVec3()) > 64.0) return false
        val level = player.level()
        if (level.isClientSide || level !is ServerLevel) return true
        val c = level.getChunk(pos)
        val data = c.getExistingData(Attachments.COVERS)
        if (data.isEmpty) return false
        val existing = data.get().map[CoverAttachment.pack(dir, pos, c.pos)] ?: return false
        return cover == null || existing.cover == cover.cover
    }
}