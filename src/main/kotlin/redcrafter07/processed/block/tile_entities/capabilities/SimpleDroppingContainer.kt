package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

class SimpleDroppingContainer() : SimpleContainer() {
    fun appendItem(itemStack: ItemStack) {
        val stack = super.addItem(itemStack)
        if (!stack.isEmpty) items.add(stack)
    }
}