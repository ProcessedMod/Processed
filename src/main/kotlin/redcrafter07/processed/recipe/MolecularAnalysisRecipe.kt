package redcrafter07.processed.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.nonNullListOf

class MolecularAnalysisRecipe(
    val research: ResourceLocation,
    energyUsage: Int,
    processingTime: Int,
    baseTier: ProcessedTier
) : ProcessedRecipe<SingleRecipeInput>(energyUsage, processingTime, baseTier) {
    val result: ItemStack = ModItems.MATERIAL_ANALYSIS.get().defaultInstance
    val inputStack: ItemStack = ModItems.MASS_SPECTROMETRY_DATA.get().defaultInstance

    init {
        result.set(ModDataComponents.RESEARCH, research)
        inputStack.set(ModDataComponents.RESEARCH, research)
    }

    override fun getRemainingItems(
        input: TieredInput<SingleRecipeInput>, random: RandomSource
    ): NonNullList<ItemStack> {
        val item = input.getItem(0).craftingRemainingItem
        return if (item.isEmpty) nonNullListOf() else nonNullListOf(item)
    }

    override fun matches(
        input: TieredInput<SingleRecipeInput>, ignored: Level
    ) = input.canCraftRecipe(baseTier) && ItemStack.isSameItemSameComponents(inputStack, input.input.item())

    override fun assemble(ignored0: TieredInput<SingleRecipeInput>, lookup: HolderLookup.Provider): ItemStack =
        result.copy()

    override fun canCraftInDimensions(p0: Int, p1: Int) = true
    override fun getResultItem(p0: HolderLookup.Provider): ItemStack = result.copy()

    override fun getSerializer() = Serializer

    override fun getType(): RecipeType<*> = ModRecipes.MOLECULAR_ANALYSIS.type

    object Serializer : RecipeSerializer<MolecularAnalysisRecipe> {
        val CODEC: MapCodec<MolecularAnalysisRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                ResourceLocation.CODEC.fieldOf("research").forGetter(MolecularAnalysisRecipe::research),
                energy(),
                processing(),
                tier(),
            ).apply(it, ::MolecularAnalysisRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MolecularAnalysisRecipe> = streamComp(
            ResourceLocation.STREAM_CODEC,
            MolecularAnalysisRecipe::research,
            ::MolecularAnalysisRecipe
        ).cast()

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }
}