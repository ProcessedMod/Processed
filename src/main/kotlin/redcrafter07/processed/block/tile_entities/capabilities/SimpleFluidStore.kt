package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.math.min

open class SimpleFluidStore(var tanks: NonNullList<FluidStack>, val capacity: Int) :
    ProcessedFluidHandler<CompoundTag>() {
    constructor(tanks: Int, capacity: Int) : this(NonNullList.withSize(tanks, FluidStack.EMPTY), capacity)

    protected fun invalidSlotIndex(slot: Int): Boolean {
        return (slot < 0 || slot >= this.tanks.size)
    }

    override fun getTanks(): Int {
        return tanks.size
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        if (invalidSlotIndex(tank)) return FluidStack.EMPTY
        return tanks[tank]
    }

    override fun getTankCapacity(tank: Int): Int {
        if (invalidSlotIndex(tank)) return 0
        return capacity
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        return true
    }

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (stack.isEmpty || stack.amount < 1) return 0
        var fluidLeft = stack.amount

        for (slot in tanks.indices) {
            val amountInTank = tanks[slot].amount

            if (amountInTank >= capacity) continue
            if (tanks[slot].isEmpty || FluidStack.isSameFluidSameComponents(tanks[slot], stack)) {
                val amount = min(capacity - amountInTank, fluidLeft)
                fluidLeft -= amount

                if (action.execute()) {
                    tanks[slot] = stack.copyWithAmount(amount + amountInTank)
                    setChanged(slot)
                }
            }

            if (fluidLeft < 1) break
        }

        return stack.amount - fluidLeft
    }

    override fun drain(stack: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if (stack.isEmpty) return drain(stack.amount, action)
        var fluid = FluidStack.EMPTY
        var amountLeft = stack.amount

        for (slot in tanks.indices) {
            if (tanks[slot].isEmpty || tanks[slot].amount < 1 || !FluidStack.isSameFluidSameComponents(
                    tanks[slot], stack
                )
            ) continue

            if (tanks[slot].amount <= amountLeft) {
                if (fluid.isEmpty) fluid = stack.copyWithAmount(tanks[slot].amount)
                else fluid.amount += tanks[slot].amount
                amountLeft -= tanks[slot].amount

                if (action.execute()) {
                    tanks[slot] = FluidStack.EMPTY
                    setChanged(slot)
                }

                if (amountLeft < 1) break
            } else {
                if (fluid.isEmpty) fluid = stack.copyWithAmount(amountLeft)
                else fluid.amount += amountLeft

                if (action.execute()) {
                    tanks[slot].amount -= amountLeft
                    setChanged(slot)
                }

                break
            }
        }

        return fluid
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        var fluid = FluidStack.EMPTY
        var amountLeft = maxDrain

        for (slot in tanks.indices) {
            if (tanks[slot].isEmpty || tanks[slot].amount < 1) continue

            if (tanks[slot].amount <= amountLeft) {
                if (fluid.isEmpty) fluid = tanks[slot].copyWithAmount(tanks[slot].amount)
                else fluid.amount += tanks[slot].amount
                amountLeft -= tanks[slot].amount

                if (action.execute()) {
                    tanks[slot] = FluidStack.EMPTY
                    setChanged(slot)
                }

                if (amountLeft < 1) break
            } else {
                if (fluid.isEmpty) fluid = tanks[slot].copyWithAmount(amountLeft)
                else fluid.amount += amountLeft

                if (action.execute()) {
                    tanks[slot].amount -= amountLeft
                    setChanged(slot)
                }

                break
            }
        }

        return fluid
    }

    override fun setFluidInTank(tank: Int, stack: FluidStack) {
        if (invalidSlotIndex(tank)) return
        tanks[tank] = stack
        setChanged(tank)
    }

    override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
        val nbtListTag = ListTag()

        for (i in tanks.indices) {
            if (!tanks[i].isEmpty && tanks[i].amount > 0) {
                val tag = CompoundTag()
                tag.putInt("Slot", i)
                tag.put("Fluid", tanks[i].saveOptional(provider))
                nbtListTag.add(tag)
            }
        }

        val nbt = CompoundTag()
        nbt.put("Fluids", nbtListTag)
        nbt.putInt("Size", tanks.size)
        return nbt
    }

    override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag) {
        setSize(if (nbt.contains("Size", 3)) nbt.getInt("Size") else tanks.size)
        val tagList = nbt.getList("Fluids", 10)

        for (i in tagList.indices) {
            val tag = tagList.getCompound(i)
            val slot = tag.getInt("Slot")
            if (slot >= 0 && slot < tanks.size) tanks[slot] =
                FluidStack.parseOptional(provider, tag.getCompound("Fluid"))
        }
    }

    fun setSize(newSize: Int) {
        tanks = NonNullList.withSize(newSize, FluidStack.EMPTY)
        setChanged(-1)
    }
}