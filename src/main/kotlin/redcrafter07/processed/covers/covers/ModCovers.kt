package redcrafter07.processed.covers.covers

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.covers.Cover
import redcrafter07.processed.covers.CoverBlock
import redcrafter07.processed.covers.CoverItem
import redcrafter07.processed.items.ModItems
import java.util.function.Supplier

object ModCovers {
    val COVERS: DeferredRegister<Cover> = DeferredRegister.create(Cover.REGISTRY, ProcessedMod.ID)
    val FLUID_COVER = register("fluid_cover", ::FluidCover)

    private fun <T : Cover> register(
        name: String, cover: (CoverBlock) -> T, coverItem: (DeferredHolder<Cover, T>) -> Item
    ): DeferredHolder<Cover, T> {
        val block = ModBlocks.BLOCKS.register(name, ::CoverBlock)
        val cover = COVERS.register(name, Supplier { cover(block.get()) })
        ModItems.registerItem(name) { coverItem(cover) }
        return cover
    }

    private fun <T : Cover> register(name: String, cover: (CoverBlock) -> T) = register(name, cover, ::CoverItem)
}