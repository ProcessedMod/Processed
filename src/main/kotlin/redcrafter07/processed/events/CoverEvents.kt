package redcrafter07.processed.events

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockDropsEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.level.ChunkWatchEvent
import redcrafter07.processed.Attachments
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.network.RPCFunctions
import kotlin.math.floor

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = ProcessedMod.ID)
object CoverEvents {
    private var lastDropLocation: BlockPos? = null
    private val drops = arrayListOf<ItemStack>()

    @SubscribeEvent
    fun onBreak(e: BlockEvent.BreakEvent) {
        val level = e.level
        if (level.isClientSide || level !is ServerLevel) return
        val c = level.getChunk(e.pos)
        val data = c.getExistingData(Attachments.COVERS)
        if (!data.isPresent) return

        for (p in level.chunkSource.chunkMap.getPlayers(c.pos, false)) RPCFunctions.coverRemoved.sendToClient(p, e.pos)

        if (e.player.isCreative) {
            data.get().removeCovers(e.pos, c.pos) {}
            c.isUnsaved = true
            return
        }

        lastDropLocation = e.pos
        drops.clear()
        data.get().removeCovers(e.pos, c.pos, drops::addAll)
        c.isUnsaved = true
    }

    @SubscribeEvent
    fun addDrops(e: BlockDropsEvent) {
        val loc = lastDropLocation ?: return
        if (e.pos != loc) return
        lastDropLocation = null
        val level = e.level
        val x = loc.x.toDouble()
        val y = loc.y.toDouble()
        val z = loc.z.toDouble()
        for (stack in drops) {
            // from Containers
            val width = EntityType.ITEM.width.toDouble()
            val d1 = 1.0 - width
            val halfWidth = width / 2.0
            val entX = floor(x) + level.random.nextDouble() * d1 + halfWidth
            val entY = floor(y) + level.random.nextDouble() * d1
            val entZ = floor(z) + level.random.nextDouble() * d1 + halfWidth

            while (!stack.isEmpty) {
                val entity = ItemEntity(level, entX, entY, entZ, stack.split(level.random.nextInt(21) + 10))
                entity.setDeltaMovement(
                    level.random.triangle(0.0, 0.11485000171139836),
                    level.random.triangle(0.2, 0.11485000171139836),
                    level.random.triangle(0.0, 0.11485000171139836)
                )
                level.addFreshEntity(entity)
                e.drops.add(entity)
            }
        }
        drops.clear()
    }

    @SubscribeEvent
    fun sync(e: ChunkWatchEvent.Sent) {
        val c = e.chunk.getExistingData(Attachments.COVERS)
        if (c.isEmpty) return
        RPCFunctions.syncCovers.sendToClient(
            e.player, e.chunk.pos, c.get().asClient()
        )
    }
}