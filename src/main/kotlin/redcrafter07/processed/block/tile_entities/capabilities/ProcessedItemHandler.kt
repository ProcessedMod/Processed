package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.items.IItemHandlerModifiable

abstract class ProcessedItemHandler<T: Tag>: IItemHandlerModifiable, INBTSerializable<T> {
    private var onChangeHandler: Runnable? = null

    fun setOnChange(newOnChangeHandler: Runnable?) {
        onChangeHandler = newOnChangeHandler
    }

    protected fun setChanged(slot: Int) {
        onChangeHandler?.run()
    }
}