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
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.nonNullListOf

class MassSpectrometerRecipe(
    val ingredient: Ingredient,
    val research: ResourceLocation,
    energyUsage: Int,
    processingTime: Int,
    baseTier: ProcessedTier
) : ProcessedRecipe<SingleRecipeInput>(energyUsage, processingTime, baseTier) {
    val result: ItemStack = ModItems.MASS_SPECTROMETRY_DATA.get().defaultInstance

    init {
        result.set(ModDataComponents.RESEARCH, research)
    }

    override fun getRemainingItems(
        input: TieredInput<SingleRecipeInput>, random: RandomSource
    ): NonNullList<ItemStack> {
        val item = input.getItem(0).craftingRemainingItem
        return if (item.isEmpty) nonNullListOf() else nonNullListOf(item)
    }

    override fun matches(
        input: TieredInput<SingleRecipeInput>, ignored: Level
    ) = input.canCraftRecipe(baseTier) && ingredient.test(input.input.item())

    override fun assemble(ignored0: TieredInput<SingleRecipeInput>, lookup: HolderLookup.Provider): ItemStack =
        result.copy()

    override fun canCraftInDimensions(p0: Int, p1: Int) = true
    override fun getResultItem(p0: HolderLookup.Provider): ItemStack = result.copy()

    override fun getSerializer() = Serializer

    override fun getType(): RecipeType<*> = ModRecipes.MASS_SPECTROMETRY.type

    object Serializer : RecipeSerializer<MassSpectrometerRecipe> {
        val CODEC: MapCodec<MassSpectrometerRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(MassSpectrometerRecipe::ingredient),
                ResourceLocation.CODEC.fieldOf("research").forGetter(MassSpectrometerRecipe::research),
                energy(),
                processing(),
                tier(),
            ).apply(it, ::MassSpectrometerRecipe)
        }

        val STREAM_CODEC = streamComp(
            Ingredient.CONTENTS_STREAM_CODEC,
            MassSpectrometerRecipe::ingredient,
            ResourceLocation.STREAM_CODEC.cast(),
            MassSpectrometerRecipe::research,
            ::MassSpectrometerRecipe
        )

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }
}