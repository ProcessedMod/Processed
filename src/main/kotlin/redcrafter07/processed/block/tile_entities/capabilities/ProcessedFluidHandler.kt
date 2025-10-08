package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable

abstract class ProcessedFluidHandler<T : Tag> : FluidHandlerModifiable, INBTSerializable<T> {
    private var onChangeHandler: Runnable? = null

    fun setOnChange(handler: Runnable?) {
        onChangeHandler = handler
    }

    protected fun setChanged(slot: Int) {
        onChangeHandler?.run()
    }
}