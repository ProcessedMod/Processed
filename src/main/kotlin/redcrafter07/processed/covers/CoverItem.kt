package redcrafter07.processed.covers

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.registries.DeferredHolder
import redcrafter07.processed.Attachments

class CoverItem(val cover: DeferredHolder<Cover, Cover>) : Item(Properties()) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val pos = context.clickedPos
        val dir = context.clickedFace
        val level = context.level
        val state = level.getBlockState(pos)
        if (state.isEmpty || state.isAir || state.canBeReplaced()) return InteractionResult.PASS
        val chunk = level.getChunk(pos)
        if (!level.isClientSide && level is ServerLevel) {
            val d = chunk.getData(Attachments.COVERS)
            if (d.placeCover(pos, dir, chunk.pos, level) { cover.get().makeCoverBehavior(pos, level, dir) }) {
                context.itemInHand.shrink(1)
                chunk.isUnsaved = true
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }
}