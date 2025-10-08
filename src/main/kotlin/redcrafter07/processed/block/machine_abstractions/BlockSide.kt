package redcrafter07.processed.block.machine_abstractions

import io.netty.buffer.ByteBuf
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.TranslatableEnum
import org.joml.Vector2i
import redcrafter07.processed.Translations
import redcrafter07.processed.getFacingDirection
import java.util.function.IntFunction

enum class BlockSide(val id: Int, val sideName: String) : StringRepresentable, TranslatableEnum {
    Top(0, "top"), Bottom(1, "bottom"), Left(2, "left"), Right(3, "right"), Front(4, "front"), Back(5, "back");

    override fun getSerializedName(): String = sideName
    val buttonPos: Vector2i
        get() = when (this) {
            Top -> Vector2i(31, 30)
            Bottom -> Vector2i(31, 72)
            Left -> Vector2i(10, 51)
            Front -> Vector2i(31, 51)
            Right -> Vector2i(52, 51)
            Back -> Vector2i(73, 51)
        }

    override fun getTranslatedName(): Component = Translations.blockSide(sideName)

    companion object {
        val BY_ID: IntFunction<BlockSide> =
            ByIdMap.continuous(BlockSide::id, BlockSide.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
        val STREAM_CODEC: StreamCodec<ByteBuf, BlockSide> = ByteBufCodecs.idMapper(BY_ID, BlockSide::id)


        // translation: IoSide <=> Direction
        // Down:    Bottom
        // Top:     Up
        // Front:   North
        // Back:    South
        // Left:    East
        // Right:   West
        // 
        // Direction translation Matrix. Do LOOKUP[Facing.3dData][Direction.3dData] and you get the "real" direction! (its like Direction + Direction)
        val DIRECTION_LOOKUP = listOf(
            listOf( // ---{ Facing: Down }---
                Direction.NORTH,  // Bottom: Front (North)
                Direction.SOUTH,  // Top: Back (South)
                Direction.UP,  // North: Up (Up)
                Direction.DOWN,  // South: Down (Down)
                Direction.WEST,  // West: Right (West)
                Direction.EAST // East: Left (East)
            ),

            listOf( // ---{ Facing: Up }---
                Direction.SOUTH,  // Bottom: Back (South)
                Direction.NORTH,  // Top: Front (North)
                Direction.DOWN,  // North: Down (Down)
                Direction.UP,  // South: Up (Up)
                Direction.WEST,  // West: Right (West)
                Direction.EAST // East: Left (East)
            ),

            listOf( // ---{ Facing: North }---
                Direction.DOWN,  // Bottom: Down (Down)
                Direction.UP,  // Top: Top (Up)
                Direction.NORTH,  // North: Front (North)
                Direction.SOUTH,  // South: Back (South)
                Direction.WEST,  // West: Right (West)
                Direction.EAST // East: Left (East)
            ),

            listOf( // ---{ Facing: South }---
                Direction.DOWN,  // Bottom: Down (Down)
                Direction.UP,  // Top: Up (Up)
                Direction.SOUTH,  // North: Back (South)
                Direction.NORTH,  // South: Front (North)
                Direction.EAST,  // West: Left (East)
                Direction.WEST // East: Right (West)
            ),

            listOf( // ---{ Facing: West }---
                Direction.DOWN,  // Bottom: Down (Down)
                Direction.UP,  // Top: Up (Up)
                Direction.EAST,  // North: Left (East)
                Direction.WEST,  // South: Right (West)
                Direction.NORTH,  // West: Front (North)
                Direction.SOUTH // East: Back (South)
            ),

            listOf( // ---{ Facing: East }---
                Direction.DOWN,  // Bottom: Down (Down)
                Direction.UP,  // Top: Up (Up)
                Direction.WEST,  // North: Left (West)
                Direction.EAST,  // South: Right (East)
                Direction.SOUTH,  // West: Back (South)
                Direction.NORTH // East: Front (North)
            )
        )

        fun fromDirection(direction: Direction): BlockSide = when (direction) {
            Direction.UP -> Top
            Direction.DOWN -> Bottom
            Direction.NORTH -> Front
            Direction.SOUTH -> Back
            Direction.WEST -> Left
            Direction.EAST -> Right
        }

        fun getFacing(machineFacing: Direction, direction: Direction): BlockSide = fromDirection(DIRECTION_LOOKUP[machineFacing.get3DDataValue()][direction.get3DDataValue()])
        fun translateDirection(direction: Direction, state: BlockState) = getFacing(getFacingDirection(state), direction)
    }
}