package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine

class MergedIoFluidCapability(val handlers: ProcessedMachine.CapabilityHandlers): FluidHandlerModifiable {

    private fun get(state: IoState): IFluidHandler = handlers.getFluidHandlerForState(state) ?: EmptyFluidHandler.INSTANCE

    override fun getTanks(): Int {
        return get(IoState.Output).tanks + get(IoState.Input).tanks
    }

    override fun getFluidInTank(slot: Int): FluidStack {
        val handler = handlers.getFluidHandlerForState(IoState.Output) ?: return FluidStack.EMPTY
        if (slot >= handler.tanks || slot < 0) return FluidStack.EMPTY
        return handler.getFluidInTank(slot)
    }

    override fun setFluidInTank(tank: Int, stack: FluidStack) {
        val realSlot = tank - get(IoState.Output).tanks

        val handler = get(IoState.Input)
        if (handler is FluidHandlerModifiable) {
            if (realSlot < 0 || realSlot >= handler.tanks) return
            handler.setFluidInTank(realSlot, stack)
        }
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return get(IoState.Input).fill(resource, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return get(IoState.Output).drain(maxDrain, action)
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return get(IoState.Output).drain(resource, action)
    }

    override fun getTankCapacity(slot: Int): Int {
        val handlerInput = get(IoState.Input)
        val handlerOutput = get(IoState.Output)

        if (slot < 0) return 0
        if (slot < handlerOutput.tanks) return handlerOutput.getTankCapacity(slot)
        if (slot < handlerInput.tanks + handlerOutput.tanks) return handlerInput.getTankCapacity(slot - handlerOutput.tanks)
        return 0
    }

    override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
        val handlerInput = get(IoState.Input)
        val handlerOutput = get(IoState.Output)

        if (slot < 0) return false
        if (slot < handlerOutput.tanks) return handlerOutput.isFluidValid(slot, stack)
        if (slot < handlerInput.tanks + handlerOutput.tanks) return handlerInput.isFluidValid(
            slot - handlerOutput.tanks,
            stack
        )
        return false
    }
}