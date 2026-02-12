package redcrafter07.processed.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import redcrafter07.processed.ProcessedTier
import java.util.*
import kotlin.jvm.optionals.getOrDefault

class PurifyingRecipe(
    val ingredient: SizedIngredient,
    val guaranteedItem: ItemStack,
    val extraOutputs: List<ChanceItemRecipeOutput>,
    energyUsage: Int, processingTime: Int, baseTier: ProcessedTier,
) : ProcessedRecipe<SingleRecipeInput>(energyUsage, processingTime, baseTier) {
    override fun matches(input: TieredInput<SingleRecipeInput>, level: Level): Boolean =
        input.canCraftRecipe(baseTier) && ingredient.test(input.input.item())

    override fun assemble(input: TieredInput<SingleRecipeInput>, registries: HolderLookup.Provider): ItemStack =
        guaranteedItem.copy()

    override fun canCraftInDimensions(width: Int, height: Int) = true
    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = guaranteedItem.copy()

    override fun getRemainingItems(
        input: TieredInput<SingleRecipeInput>, random: RandomSource
    ): NonNullList<ItemStack> {
        val list = NonNullList.createWithCapacity<ItemStack>(extraOutputs.size)
        for (extra in extraOutputs) {
            if (extra.probability >= 100) list.add(extra.item.copy())
            else if (extra.probability <= 0) continue
            else if (random.nextIntBetweenInclusive(0, 99) < extra.probability) list.add(extra.item.copy())
        }
        return list
    }

    override fun getSerializer(): RecipeSerializer<PurifyingRecipe> = ModRecipes.PURIFYING.serializer
    override fun getType(): RecipeType<PurifyingRecipe> = ModRecipes.PURIFYING.type

    object Serializer : RecipeSerializer<PurifyingRecipe> {
        val CODEC: MapCodec<PurifyingRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(PurifyingRecipe::ingredient),
                ItemStack.OPTIONAL_CODEC.lenientOptionalFieldOf("guaranteedItem").xmap(
                    { opt -> opt.getOrDefault(ItemStack.EMPTY) }, Optional<ItemStack>::of
                ).forGetter(PurifyingRecipe::guaranteedItem),
                ChanceItemRecipeOutput.LIST_CODEC.fieldOf("extraOutputs").forGetter(PurifyingRecipe::extraOutputs),
                energy(),
                processing(),
                tier()
            ).apply(it, ::PurifyingRecipe)
        }
        val STREAM_CODEC = streamComp(
            SizedIngredient.STREAM_CODEC,
            PurifyingRecipe::ingredient,
            ItemStack.OPTIONAL_STREAM_CODEC,
            PurifyingRecipe::guaranteedItem,
            ChanceItemRecipeOutput.LIST_STREAM_CODEC,
            PurifyingRecipe::extraOutputs,
            ::PurifyingRecipe
        )

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }

}