package redcrafter07.processed.recipe

import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import redcrafter07.processed.items.MinerItem
import redcrafter07.processed.items.ModDataComponents
import kotlin.math.floor

object MinerFormRecipe : CustomRecipe(CraftingBookCategory.MISC) {
    override fun matches(
        input: CraftingInput, p1: Level
    ): Boolean {
        var hasBase = false
        var hasEngine = false

        for (item in input.items()) {
            if (item.item is MinerItem) {
                if (hasBase || item.has(ModDataComponents.MINER_DATA)) return false
                else hasBase = true
                continue
            }
            val engineData = item.get(ModDataComponents.ENGINE_TIER)
            if (!hasEngine && engineData != null) {
                hasEngine = true
                continue
            }
            return false
        }

        return hasBase && hasEngine
    }

    override fun assemble(
        input: CraftingInput, holders: HolderLookup.Provider
    ): ItemStack {
        var base: ItemStack = ItemStack.EMPTY
        for (item in input.items()) {
            if (item.item is MinerItem) {
                base = item.copy()
                break
            }
        }
        var engine: ItemStack = ItemStack.EMPTY
        for (item in input.items()) {
            if (item.has(ModDataComponents.ENGINE_TIER)) {
                engine = item
                break
            }
        }
        val minerData = MinerItem.Companion.MinerData(
            engine.itemHolder.key!!.location(), engine.get(ModDataComponents.ENGINE_TIER.get())!!
        )
        base.set(ModDataComponents.MINER_DATA, minerData)
        base.damageValue = floor(Math.random() * 100).toInt()
        return base

    }

    override fun canCraftInDimensions(width: Int, height: Int) = (width + height) >= 2

    override fun getSerializer(): RecipeSerializer<*> = ModRecipes.MINER_FORM_SERIALIZER.get()

    object Serializer : RecipeSerializer<MinerFormRecipe> {
        val CODEC: MapCodec<MinerFormRecipe> = Codec.EMPTY.xmap({ MinerFormRecipe }, { Unit.INSTANCE })
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MinerFormRecipe> = StreamCodec.unit(MinerFormRecipe)

        override fun codec(): MapCodec<MinerFormRecipe> = CODEC
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MinerFormRecipe> = STREAM_CODEC
    }
}