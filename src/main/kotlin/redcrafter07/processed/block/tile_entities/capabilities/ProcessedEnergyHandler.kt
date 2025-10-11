package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage

abstract class ProcessedEnergyHandler<T : Tag> : EnergyStorageModifiable, IEnergyStorage, INBTSerializable<T> {
    private var onChangeHandler: Runnable? = null

    fun setOnChange(newOnChangeHandler: Runnable?) {
        onChangeHandler = newOnChangeHandler
    }

    protected fun setChanged() {
        onChangeHandler?.run()
    }
}