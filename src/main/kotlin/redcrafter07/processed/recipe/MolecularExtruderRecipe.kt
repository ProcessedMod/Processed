package redcrafter07.processed.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.nonNullListOf

class MolecularExtruderRecipe(
    val research: ResourceLocation,
    val ingredient: Ingredient,
    val result: ItemStack,
    energyUsage: Int,
    processingTime: Int,
    baseTier: ProcessedTier
) : ProcessedRecipe<MolecularExtruderInput>(energyUsage, processingTime, baseTier) {
    val inputStack: ItemStack = ModItems.MATERIAL_ANALYSIS.get().defaultInstance

    init {
        inputStack.set(ModDataComponents.RESEARCH, research)
    }

    override fun getRemainingItems(
        input: TieredInput<MolecularExtruderInput>, random: RandomSource
    ): NonNullList<ItemStack> {
        val item = input.getItem(0).craftingRemainingItem
        return if (item.isEmpty) nonNullListOf() else nonNullListOf(item)
    }

    override fun matches(
        input: TieredInput<MolecularExtruderInput>, ignored: Level
    ) = input.canCraftRecipe(baseTier) && ingredient.test(input.input.item) && research == input.input.research

    override fun assemble(ignored0: TieredInput<MolecularExtruderInput>, lookup: HolderLookup.Provider): ItemStack =
        result.copy()

    override fun canCraftInDimensions(p0: Int, p1: Int) = true
    override fun getResultItem(p0: HolderLookup.Provider): ItemStack = result.copy()

    override fun getSerializer() = Serializer

    override fun getType(): RecipeType<*> = ModRecipes.MOLECULAR_EXTRUDING.type

    object Serializer : RecipeSerializer<MolecularExtruderRecipe> {
        val CODEC: MapCodec<MolecularExtruderRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                ResourceLocation.CODEC.fieldOf("research").forGetter(MolecularExtruderRecipe::research),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(MolecularExtruderRecipe::ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(MolecularExtruderRecipe::result),
                energy(),
                processing(),
                tier(),
            ).apply(it, ::MolecularExtruderRecipe)
        }

        val STREAM_CODEC = streamComp(
            ResourceLocation.STREAM_CODEC.cast(),
            MolecularExtruderRecipe::research,
            Ingredient.CONTENTS_STREAM_CODEC,
            MolecularExtruderRecipe::ingredient,
            ItemStack.STREAM_CODEC,
            MolecularExtruderRecipe::result,
            ::MolecularExtruderRecipe
        )

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }
}