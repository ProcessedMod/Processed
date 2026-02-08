package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.block.tile_entities.EnergyHatchBlockEntity
import redcrafter07.processed.block.tile_entities.OutputFluidHatchBlockEntity
import redcrafter07.processed.block.tile_entities.OutputItemHatchBlockEntity
import redcrafter07.processed.block.tile_entities.TieredRecipeBlockEntity
import kotlin.math.max
import kotlin.math.min

abstract class AbstractRecipeMultiBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(type, pos, blockState) {
    var recipeData: TieredRecipeBlockEntity.RecipeData? = null
    val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> recipeData?.progress ?: 0
            1 -> recipeData?.maxProgress ?: -1
            2 -> energyCapability.energyStored
            3 -> tier.scalePower(recipeData?.baseEnergyUsage ?: 0)
            else -> dataOverrides?.get(index - 3) ?: 0
        }

        override fun set(index: Int, value: Int) =
            if (index <= 3) Unit else dataOverrides?.set(index - 3, value) ?: Unit

        override fun getCount() = 3 + (dataOverrides?.count ?: 0)
    }

    protected open val dataOverrides: ContainerData? = null

    override fun tileTickServer(level: ServerLevel, pos: BlockPos, state: BlockState) {
        if (recipeData == null) recipeData = getRecipe(level)
        val recipeData = recipeData ?: return setWorking(level, pos, state, false)
        if (recipeData.progress >= recipeData.maxProgress) {
            if (tryInsertRecipeOutputs(level, pos, state, recipeData)) this.recipeData = getRecipe(level)
            return
        }

        if (!useScaledPower(recipeData.baseEnergyUsage)) {
            recipeData.progress = max(0, recipeData.progress - 2 * tier.speedMultiplier)
            setChanged()
            return setWorking(level, pos, state, false)
        }

        setWorking(level, pos, state, true)
        recipeData.progress = min(recipeData.progress + tier.speedMultiplier, recipeData.maxProgress)
    }

    fun useScaledPower(amount: Int): Boolean {
        val amount = tier.scalePower(amount)
        if (!isAssembled) return false
        val energyIn = specialBlocks[SpecialBlockType.EnergyInput] ?: return false
        val level = level ?: return false

        var remaining = amount
        for (block in energyIn) {
            val be = level.getBlockEntity(block)
            if (be is EnergyHatchBlockEntity) remaining -= remaining.coerceAtMost(be.handler.energyStored)
            if (remaining <= 0) break
        }
        if (remaining > 0) return false
        remaining = amount
        for (block in energyIn) {
            val be = level.getBlockEntity(block)
            if (be is EnergyHatchBlockEntity) {
                val toExtract = remaining.coerceAtMost(be.handler.energyStored)
                be.handler.energyStored -= toExtract
                remaining -= toExtract
            }
            if (remaining <= 0) break
        }

        return true
    }

    protected open fun tryInsertRecipeOutputs(
        level: Level, pos: BlockPos, state: BlockState, recipeData: TieredRecipeBlockEntity.RecipeData
    ): Boolean {
        if (!isAssembled) return false
        val fluidHandlers = specialBlocks[SpecialBlockType.FluidOutput]?.map(level::getBlockEntity)
            ?.mapNotNull { if (it is OutputFluidHatchBlockEntity) it.handler else null } ?: listOf()
        val itemHandlers = specialBlocks[SpecialBlockType.ItemOutput]?.map(level::getBlockEntity)
            ?.mapNotNull { if (it is OutputItemHatchBlockEntity) it.handler else null } ?: listOf()

        for (item in recipeData.outputItems) {
            if (item.isEmpty) continue
            val item = item.copy()

            for (itemHandler in itemHandlers) {
                for (slot in 0..<itemHandler.slots) {
                    val stack = itemHandler.getStackInSlot(slot)
                    if (stack.isEmpty) item.shrink(item.maxStackSize)
                    else if (ItemStack.isSameItemSameComponents(
                            stack, item
                        )
                    ) item.shrink(max(stack.maxStackSize - stack.count, 0))

                    if (item.isEmpty) break
                }
                if (item.isEmpty) break
            }
            if (!item.isEmpty) return false
        }

        for (fluid in recipeData.outputLiquids) {
            if (fluid.isEmpty) continue
            val fluid = fluid.copy()

            for (fluidHandler in fluidHandlers) {
                for (tank in 0..<fluidHandler.getTanks()) {
                    val stack = fluidHandler.getFluidInTank(tank)
                    if (stack.isEmpty) fluid.shrink(fluidHandler.getTankCapacity(tank))
                    else if (FluidStack.isSameFluidSameComponents(
                            stack, fluid
                        )
                    ) fluid.shrink(max(fluidHandler.getTankCapacity(tank) - stack.amount, 0))

                    if (fluid.isEmpty) break
                }
                if (fluid.isEmpty) break
            }

            if (!fluid.isEmpty) return false
        }

        for (item in recipeData.outputItems) {
            if (item.isEmpty) continue
            val item = item.copy()

            for (itemHandler in itemHandlers) {
                for (slot in 0..<itemHandler.slots) {
                    val stack = itemHandler.getStackInSlot(slot)
                    if (stack.isEmpty) {
                        if (item.count <= item.maxStackSize) {
                            itemHandler.setStackInSlot(slot, item)
                            break
                        } else {
                            itemHandler.setStackInSlot(slot, item.copyWithCount(item.maxStackSize))
                            item.shrink(item.maxStackSize)
                        }
                    } else if (ItemStack.isSameItemSameComponents(stack, item)) {
                        val spaceLeft = stack.maxStackSize - stack.count
                        if (spaceLeft > 0) {
                            val amount = min(spaceLeft, item.count)
                            stack.grow(amount)
                            item.shrink(amount)
                        }
                    }

                    if (item.isEmpty) break
                }
                if (item.isEmpty) break
            }
        }

        for (fluid in recipeData.outputLiquids) {
            if (fluid.isEmpty) continue

            val fluid = fluid.copy()

            for (fluidHandler in fluidHandlers) {
                for (tank in 0..<fluidHandler.getTanks()) {
                    val stack = fluidHandler.getFluidInTank(tank)
                    if (stack.isEmpty) {
                        if (fluid.amount <= fluidHandler.getTankCapacity(tank)) {
                            fluidHandler.setFluidInTank(tank, fluid)
                            break
                        } else {
                            fluidHandler.setFluidInTank(tank, fluid.copyWithAmount(fluidHandler.getTankCapacity(tank)))
                            fluid.shrink(fluidHandler.getTankCapacity(tank))
                        }
                    } else if (FluidStack.isSameFluidSameComponents(stack, fluid)) {
                        val spaceLeft = fluidHandler.getTankCapacity(tank) - stack.amount
                        if (spaceLeft > 0) {
                            val amount = min(spaceLeft, fluid.amount)
                            stack.grow(amount)
                            fluid.shrink(amount)
                        }
                    }

                    if (fluid.isEmpty) break
                }
                if (fluid.isEmpty) break
            }
        }

        return true
    }

    fun setWorking(level: Level, pos: BlockPos, state: BlockState, working: Boolean) {
        if (state.getValue(BlockProperties.WORKING) == working) return
        level.setBlockAndUpdate(pos, state.setValue(BlockProperties.WORKING, working))
    }

    /** Gets a recipe and removes it's inputs */
    protected abstract fun getRecipe(level: Level): TieredRecipeBlockEntity.RecipeData?

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        val recipeData = recipeData ?: return
        tag.put("recipe", recipeData.serialize(provider))
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        recipeData = if (tag.contains("recipe", CompoundTag.TAG_COMPOUND.toInt())) {
            TieredRecipeBlockEntity.RecipeData.deserialize(tag.getCompound("recipe"), registries)
        } else null
    }
}