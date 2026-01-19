package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.SimpleFluidStore

class FluidTankBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntities.FLUID_TANK.get(), pos, state), FluidCapableBlockEntity {
    val fluidHandler: SimpleFluidStore = SimpleFluidStore(1, 80000)

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    fun useItemOn(stack: ItemStack, player: Player, hand: InteractionHand): ItemInteractionResult? {
        val fluidCapability = stack.getCapability(Capabilities.FluidHandler.ITEM) ?: return null
        val capacity = fluidHandler.getTankCapacity(0)
        val fluid = fluidHandler.getFluidInTank(0)
        var extractionSuccess = false
        if (fluid.isEmpty) {
            val extracted = fluidCapability.drain(capacity, IFluidHandler.FluidAction.EXECUTE)
            if (extracted.amount > 0 && !extracted.isEmpty) {
                fluidHandler.setFluidInTank(0, extracted)
                extractionSuccess = true
            }
        } else {
            val extracted = fluidCapability.drain(
                fluid.copyWithAmount(capacity - fluid.amount), IFluidHandler.FluidAction.SIMULATE
            )
            if (extracted.amount <= capacity - fluid.amount && extracted.amount > 0 && !extracted.isEmpty) {
                fluidHandler.setFluidInTank(0, fluid.copyWithAmount(fluid.amount + extracted.amount))
                extractionSuccess = true
                fluidCapability.drain(
                    fluid.copyWithAmount(capacity - fluid.amount), IFluidHandler.FluidAction.EXECUTE
                )
            }
        }
        if (extractionSuccess) {
            if (!player.isCreative) {
                val container = fluidCapability.container
                if (!container.isEmpty) {
                    if (stack.count == 1) player.setItemInHand(hand, container)
                    else if (player.getInventory().add(container)) stack.shrink(1)
                } else {
                    stack.shrink(1)
                    if (stack.isEmpty) player.setItemInHand(hand, ItemStack.EMPTY)
                }
            }
            return ItemInteractionResult.SUCCESS
        }

        val filled = fluidCapability.fill(fluid, IFluidHandler.FluidAction.EXECUTE)
        if (filled > 0) {
            if (fluid.amount - filled <= 0) fluidHandler.setFluidInTank(0, FluidStack.EMPTY)
            else fluidHandler.setFluidInTank(0, fluid.copyWithAmount(fluid.amount - filled))
            val container = fluidCapability.container
            if (!player.isCreative) {
                if (stack.count == 1) player.setItemInHand(hand, container)
                else if (stack.count > 1 && !player.getInventory().add(container)) stack.shrink(1)
                else {
                    player.drop(container, false, true)
                    stack.shrink(1)
                }
            }
            return ItemInteractionResult.SUCCESS
        }
        return null
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        fluidHandler.deserializeNBT(registries, tag.getCompound("FluidStorage"))
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("FluidStorage", fluidHandler.serializeNBT(registries))
    }

    override fun fluidCapabilityForSide(side: BlockSide?, state: BlockState): IFluidHandler {
        return fluidHandler
    }

    val size: Float
        get() = (fluidHandler.getFluidInTank(0).amount.toFloat()) / (fluidHandler.getTankCapacity(0).toFloat())
}

