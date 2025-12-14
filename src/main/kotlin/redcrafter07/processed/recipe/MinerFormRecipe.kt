package redcrafter07.processed.recipe

import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.miner.MinerData

object MinerFormRecipe : CustomRecipe(CraftingBookCategory.MISC) {
    override fun matches(input: CraftingInput, p1: Level): Boolean {
        var hasHull = false
        var hasTank = false
        var hasEngine = false
        var hasMiners = false
        var hasCargoBay = false

        for (item in input.items()) {
            if (item.has(ModDataComponents.HULL_DATA)) {
                if (hasHull) return false
                hasHull = true
            } else if (item.has(ModDataComponents.TANK_DATA)) {
                if (hasTank) return false
                hasTank = true
            } else if (item.has(ModDataComponents.ENGINE_DATA)) {
                if (hasEngine) return false
                hasEngine = true
            } else if (item.has(ModDataComponents.MINER_DATA)) {
                if (hasMiners) return false
                hasMiners = true
            } else if (item.has(ModDataComponents.CARGO_BAY_DATA)) {
                if (hasCargoBay) return false
                hasCargoBay = true
            } else if (item.isEmpty) continue
            else return false
        }

        return hasHull && hasTank && hasEngine && hasMiners && hasCargoBay
    }

    override fun assemble(input: CraftingInput, holders: HolderLookup.Provider): ItemStack {
        var hull: ResourceLocation? = null
        var tank: ResourceLocation? = null
        var engine: ResourceLocation? = null
        var miners: ResourceLocation? = null
        var cargoBay: ResourceLocation? = null
        val comps = DataComponentPatch.builder()

        for (item in input.items()) {
            val hullData = item.get(ModDataComponents.HULL_DATA)
            if (hullData != null) {
                hull = id(item)
                comps.set(ModDataComponents.HULL_DATA.get(), hullData.copy())
                continue
            }
            val tankData = item.get(ModDataComponents.TANK_DATA)
            if (tankData != null) {
                tank = id(item)
                comps.set(ModDataComponents.TANK_DATA.get(), tankData.copy())
                continue
            }
            val engineData = item.get(ModDataComponents.ENGINE_DATA)
            if (engineData != null) {
                engine = id(item)
                comps.set(ModDataComponents.ENGINE_DATA.get(), engineData.copy())
                continue
            }
            val minerData = item.get(ModDataComponents.MINER_DATA)
            if (minerData != null) {
                miners = id(item)
                comps.set(ModDataComponents.MINER_DATA.get(), minerData.copy())
                continue
            }
            val cargoBayData = item.get(ModDataComponents.CARGO_BAY_DATA)
            if (cargoBayData != null) {
                cargoBay = id(item)
                comps.set(ModDataComponents.CARGO_BAY_DATA.get(), cargoBayData.copy())
                continue
            }
        }

        if (hull == null || tank == null || engine == null || miners == null || cargoBay == null) return ItemStack.EMPTY
        comps.set(
            ModDataComponents.ASSEMBLED_MINER.get(),
            MinerData.Assembled(hull, tank, engine, miners, cargoBay, FluidStack.EMPTY)
        )
        val stack = ModItems.ASSEMBLED_MINER.get().defaultInstance
        stack.applyComponents(comps.build())
        return stack
    }

    fun id(stack: ItemStack): ResourceLocation = BuiltInRegistries.ITEM.getKey(stack.item)

    override fun canCraftInDimensions(width: Int, height: Int) = (width * height) >= 5

    override fun getSerializer(): RecipeSerializer<*> = ModRecipes.MINER_FORM_SERIALIZER.get()

    object Serializer : RecipeSerializer<MinerFormRecipe> {
        val CODEC: MapCodec<MinerFormRecipe> = Codec.EMPTY.xmap({ MinerFormRecipe }, { Unit.INSTANCE })
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MinerFormRecipe> = StreamCodec.unit(MinerFormRecipe)

        override fun codec(): MapCodec<MinerFormRecipe> = CODEC
        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MinerFormRecipe> = STREAM_CODEC
    }
}