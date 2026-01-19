package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.tile_entities.FluidHatch
import redcrafter07.processed.gui.widgets.FluidWidget.InsertionKind
import redcrafter07.processed.rl

// TODO: Turn this and SetFluidMenuContentsPacket into RPC Methods on the AbstractFluidContainerMenu and add the ability for super classes to also have @RPCMethods.
class FluidHandlerClickPacket(val pos: BlockPos, val insertionKind: InsertionKind) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<FluidHandlerClickPacket> =
            CustomPacketPayload.Type(rl("fluid_handler_click"))
        val CODEC: StreamCodec<ByteBuf, FluidHandlerClickPacket> = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            FluidHandlerClickPacket::pos,
            InsertionKind.STREAM_CODEC,
            FluidHandlerClickPacket::insertionKind,
            ::FluidHandlerClickPacket
        )
    }

    fun handleServer(context: IPayloadContext) {
        val p = context.player()
        if (p !is ServerPlayer) return
        if (!p.hasContainerOpen()) return
        var carried = p.containerMenu.carried.copy()

        val state = p.level().getBlockState(pos)
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val cap =
            when (val be = p.level().getBlockEntity(pos)) {
                is FluidHatch -> be.inventoryHandler()
                is FluidCapableBlockEntity -> be.fluidCapabilityForSide(null, state)
                else -> p.level().getCapability(Capabilities.FluidHandler.BLOCK, pos, state, be, null)
            }
        if (cap == null) return
        val itemCap = carried.getCapability(Capabilities.FluidHandler.ITEM)
        if (itemCap == null) {
            // buckets
            val itemItem = carried.item
            if (itemItem !is BucketItem) return
            val kind = if (insertionKind == InsertionKind.Both) {
                if (itemItem.content.isSame(Fluids.EMPTY)) InsertionKind.ExtractOnly
                else InsertionKind.InsertOnly
            } else insertionKind

            when (kind) {
                InsertionKind.InsertOnly -> {
                    if (itemItem.content.isSame(Fluids.EMPTY) || cap.fill(
                            FluidStack(itemItem.content, 1000), FluidAction.SIMULATE
                        ) != 1000
                    ) return
                    cap.fill(FluidStack(itemItem.content, 1000), FluidAction.EXECUTE)
                    carried = Items.BUCKET.defaultInstance
                }

                InsertionKind.ExtractOnly -> {
                    if (!itemItem.content.isSame(Fluids.EMPTY)) return
                    val fluid = cap.drain(1000, FluidAction.SIMULATE)
                    if (fluid.amount != 1000) return
                    cap.drain(1000, FluidAction.EXECUTE)
                    carried = fluid.fluid.bucket.defaultInstance
                }

                else -> throw IllegalStateException()
            }
        } else when (insertionKind) {
            InsertionKind.InsertOnly -> {
                val stack = itemCap.drain(Int.MAX_VALUE, FluidAction.SIMULATE)
                val filled = cap.fill(stack, FluidAction.EXECUTE)
                if (filled > 0) itemCap.drain(filled, FluidAction.EXECUTE)
            }

            InsertionKind.ExtractOnly -> {
                val stack = cap.drain(Int.MAX_VALUE, FluidAction.SIMULATE)
                val filled = itemCap.fill(stack, FluidAction.EXECUTE)
                if (filled > 0) cap.drain(filled, FluidAction.EXECUTE)
            }

            InsertionKind.Both -> {
                val stack = itemCap.drain(Int.MAX_VALUE, FluidAction.SIMULATE)
                val filled = cap.fill(stack, FluidAction.EXECUTE)
                if (filled > 0) itemCap.drain(filled, FluidAction.EXECUTE)
                else {
                    val stack = cap.drain(Int.MAX_VALUE, FluidAction.SIMULATE)
                    val filled = itemCap.fill(stack, FluidAction.EXECUTE)
                    if (filled > 0) cap.drain(filled, FluidAction.EXECUTE)
                }
            }
        }

        if (itemCap is IFluidHandlerItem) carried = itemCap.container

        if (!p.isCreative && !ItemStack.isSameItemSameComponents(p.containerMenu.carried, carried)) {
            p.containerMenu.carried = carried
            p.containerMenu.setRemoteCarried(carried)
        }
    }

    override fun type(): CustomPacketPayload.Type<FluidHandlerClickPacket> = TYPE
}