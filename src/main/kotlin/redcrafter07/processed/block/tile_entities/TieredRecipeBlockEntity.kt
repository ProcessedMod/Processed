package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.block.machine_abstractions.TieredProcessedMachine
import kotlin.math.max
import kotlin.math.min

abstract class TieredRecipeBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    TieredProcessedMachine(type, pos, blockState) {
    var recipeData: RecipeData? = null

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

    override fun commonTick(level: Level, pos: BlockPos, state: BlockState) {
        if (recipeData == null) recipeData = getRecipe()
        val recipeData = recipeData ?: return setWorking(level, pos, state, false)
        if (recipeData.progress >= recipeData.maxProgress) {
            if (tryInsertRecipeOutputs(level, pos, state, recipeData)) this.recipeData = getRecipe()
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

    protected open fun tryInsertRecipeOutputs(
        level: Level, pos: BlockPos, state: BlockState, recipeData: RecipeData
    ): Boolean {
        val fluidHandler = outputFluidHandler
        val itemHandler = outputItemHandler

        for (item in recipeData.outputItems) {
            if (item.isEmpty) continue
            val item = item.copy()

            for (slot in 0..<itemHandler.slots) {
                val stack = itemHandler.getStackInSlot(slot)
                if (stack.isEmpty) item.shrink(item.maxStackSize)
                else if (ItemStack.isSameItemSameComponents(stack, item)) item.shrink(max(stack.maxStackSize - stack.count, 0))

                if (item.isEmpty) break
            }
            if (!item.isEmpty) return false
        }

        for (fluid in recipeData.outputLiquids) {
            if (fluid.isEmpty) continue
            val fluid = fluid.copy()

            for (tank in 0..<fluidHandler.tanks) {
                val stack = fluidHandler.getFluidInTank(tank)
                if (stack.isEmpty) fluid.shrink(fluidHandler.getTankCapacity(tank))
                else if (FluidStack.isSameFluidSameComponents(
                        stack, fluid
                    )
                ) fluid.shrink(max(fluidHandler.getTankCapacity(tank) - stack.amount, 0))

                if (fluid.isEmpty) break
            }

            if (!fluid.isEmpty) return false
        }

        for (item in recipeData.outputItems) {
            if (item.isEmpty) continue
            val item = item.copy()

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
        }

        for (fluid in recipeData.outputLiquids) {
            if (fluid.isEmpty) continue

            val fluid = fluid.copy()

            for (tank in 0..<fluidHandler.tanks) {
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
        }

        return true
    }

    fun setWorking(level: Level, pos: BlockPos, state: BlockState, working: Boolean) {
        if (state.getValue(BlockProperties.WORKING) == working) return
        level.setBlockAndUpdate(pos, state.setValue(BlockProperties.WORKING, working))
    }

    /** Gets a recipe and removes it's inputs */
    protected abstract fun getRecipe(): RecipeData?

    private fun canInsertItemIntoOutputSlot(item: Item): Boolean {
        val outputStack = outputItemHandler.getStackInSlot(0)
        return outputStack.isEmpty || outputStack.`is`(item)
    }

    private fun canInsertAmountIntoOutputSlot(count: Int): Boolean {
        val outputStack = outputItemHandler.getStackInSlot(0)
        return outputStack.count + count <= outputItemHandler.getSlotLimit(0)
    }

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        val recipeData = recipeData ?: return
        tag.put("recipe", recipeData.serialize(provider))
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        recipeData = if (tag.contains("recipe", CompoundTag.TAG_COMPOUND.toInt())) {
            deserializeRecipeData(tag.getCompound("recipe"), registries)
        } else null
    }

    class RecipeData(
        val outputItems: List<ItemStack>,
        val outputLiquids: List<FluidStack>,
        val maxProgress: Int,
        val baseEnergyUsage: Int,
        var progress: Int = 0,
    ) {
        constructor(
            outputItems: List<ItemStack>, maxProgress: Int, baseEnergyUsage: Int
        ) : this(outputItems, listOf(), maxProgress, baseEnergyUsage)

        constructor(
            outputItem: ItemStack, maxProgress: Int, baseEnergyUsage: Int
        ) : this(listOf(outputItem), listOf(), maxProgress, baseEnergyUsage)

        constructor(
            outputLiquid: FluidStack, maxProgress: Int, baseEnergyUsage: Int
        ) : this(listOf(), listOf(outputLiquid), maxProgress, baseEnergyUsage)

        fun serialize(registries: HolderLookup.Provider): CompoundTag {
            val tag = CompoundTag()
            tag.putInt("progress", progress)
            tag.putInt("maxProgress", maxProgress)
            tag.putInt("energyUsage", baseEnergyUsage)
            val items = ListTag()
            outputItems.forEach { items.add(it.saveOptional(registries)) }
            val fluids = ListTag()
            outputLiquids.forEach { fluids.add(it.saveOptional(registries)) }
            if (outputItems.isNotEmpty()) tag.put("items", items)
            if (outputLiquids.isNotEmpty()) tag.put("fluids", fluids)
            return tag
        }
    }

    fun deserializeRecipeData(tag: CompoundTag, registries: HolderLookup.Provider): RecipeData {
        val progress = tag.getInt("progress")
        val maxProgress = tag.getInt("maxProgress")
        val energyUsage = tag.getInt("energyUsage")

        val items = arrayListOf<ItemStack>()
        val fluids = arrayListOf<FluidStack>()
        if (tag.contains("items")) {
            val itemList = tag.getList("items", CompoundTag.TAG_COMPOUND.toInt())
            itemList.forEach { if (it is CompoundTag) items.add(ItemStack.parseOptional(registries, it)) }
        }
        if (tag.contains("fluids")) {
            val fluidList = tag.getList("fluids", CompoundTag.TAG_COMPOUND.toInt())
            fluidList.forEach { if (it is CompoundTag) fluids.add(FluidStack.parseOptional(registries, it)) }
        }
        items.trimToSize()
        fluids.trimToSize()
        return RecipeData(items, fluids, maxProgress, energyUsage, progress)
    }
}