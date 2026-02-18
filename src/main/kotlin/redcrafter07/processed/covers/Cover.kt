package redcrafter07.processed.covers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.RegistryBuilder
import redcrafter07.processed.rl
import java.util.*

abstract class Cover(val block: Block, val textureOverride: ResourceLocation? = null) {
    val location = lazy { REGISTRY.getKey(this) ?: throw IllegalStateException("Unregistered cover") }
    val textureLocation = lazy { textureOverride ?: location.value.withPrefix("item/") }

    abstract fun makeCoverBehavior(position: BlockPos, level: Level, direction: Direction): CoverBehavior

    companion object {
        val REGISTRY_KEY: ResourceKey<Registry<Cover>> = ResourceKey.createRegistryKey(rl("covers"))
        val REGISTRY: Registry<Cover> = RegistryBuilder(REGISTRY_KEY).sync(true).create()

        fun openMenu(player: Player, cover: CoverBehavior): OptionalInt {
            return player.openMenu(cover) {
                it.writeBlockPos(cover.position)
                Direction.STREAM_CODEC.encode(it, cover.direction)
                cover.writeExtraData(it)
            }
        }
    }
}