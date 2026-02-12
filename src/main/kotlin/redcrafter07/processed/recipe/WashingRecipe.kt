package redcrafter07.processed.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import redcrafter07.processed.ProcessedTier
import java.util.*
import kotlin.jvm.optionals.getOrDefault

class WashingRecipe(
    val ingredient: SizedIngredient,
    val dissolver: SizedFluidIngredient,
    val guaranteedItem: ItemStack,
    val extraOutputs: List<ChanceItemRecipeOutput>,
    energyUsage: Int, processingTime: Int, baseTier: ProcessedTier,
) : ProcessedRecipe<WashingRecipeInput>(energyUsage, processingTime, baseTier) {
    override fun matches(input: TieredInput<WashingRecipeInput>, level: Level): Boolean =
        input.canCraftRecipe(baseTier) && ingredient.test(input.input.item) && dissolver.test(input.input.dissolver)

    override fun assemble(input: TieredInput<WashingRecipeInput>, registries: HolderLookup.Provider): ItemStack =
        guaranteedItem.copy()

    override fun canCraftInDimensions(width: Int, height: Int) = true
    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = guaranteedItem.copy()

    override fun getRemainingItems(
        input: TieredInput<WashingRecipeInput>, random: RandomSource
    ): NonNullList<ItemStack> {
        val list = NonNullList.createWithCapacity<ItemStack>(extraOutputs.size)
        for (extra in extraOutputs) {
            if (extra.probability >= 100) list.add(extra.item.copy())
            else if (extra.probability <= 0) continue
            else if (random.nextIntBetweenInclusive(0, 99) < extra.probability) list.add(extra.item.copy())
        }
        return list
    }

    override fun getSerializer(): RecipeSerializer<WashingRecipe> = ModRecipes.WASHING.serializer
    override fun getType(): RecipeType<WashingRecipe> = ModRecipes.WASHING.type

    object Serializer : RecipeSerializer<WashingRecipe> {
        val CODEC: MapCodec<WashingRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(WashingRecipe::ingredient),
                SizedFluidIngredient.FLAT_CODEC.fieldOf("dissolver").forGetter(WashingRecipe::dissolver),
                ItemStack.OPTIONAL_CODEC.lenientOptionalFieldOf("guaranteedItem").xmap(
                    { opt -> opt.getOrDefault(ItemStack.EMPTY) }, Optional<ItemStack>::of
                ).forGetter(WashingRecipe::guaranteedItem),
                ChanceItemRecipeOutput.LIST_CODEC.fieldOf("extraOutputs").forGetter(WashingRecipe::extraOutputs),
                energy(),
                processing(),
                tier()
            ).apply(it, ::WashingRecipe)
        }
        val STREAM_CODEC = streamComp(
            SizedIngredient.STREAM_CODEC,
            WashingRecipe::ingredient,
            SizedFluidIngredient.STREAM_CODEC,
            WashingRecipe::dissolver,
            ItemStack.OPTIONAL_STREAM_CODEC,
            WashingRecipe::guaranteedItem,
            ChanceItemRecipeOutput.LIST_STREAM_CODEC,
            WashingRecipe::extraOutputs,
            ::WashingRecipe
        )

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }
}