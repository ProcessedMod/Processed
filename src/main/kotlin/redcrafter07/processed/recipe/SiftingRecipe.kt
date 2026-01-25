package redcrafter07.processed.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import redcrafter07.processed.nonNullListOf
import java.util.Optional
import kotlin.jvm.optionals.getOrDefault

class SiftingRecipe(
    val ingredient: Ingredient,
    val guaranteedItem: ItemStack,
    val extraOutputs: List<ChanceItemRecipeOutput>,
    val processingTime: Int,
    val energyUsage: Int
) : Recipe<SingleRecipeInput> {
    override fun getIngredients() = nonNullListOf(ingredient)
    override fun matches(input: SingleRecipeInput, level: Level) = ingredient.test(input.item())
    override fun assemble(input: SingleRecipeInput, registries: HolderLookup.Provider): ItemStack =
        guaranteedItem.copy()

    override fun canCraftInDimensions(width: Int, height: Int) = true
    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = guaranteedItem.copy()

    @Deprecated(
        message = "getRemainingItems without a level or random source is deprecated.",
        replaceWith = ReplaceWith("getRemainingItems")
    )
    override fun getRemainingItems(input: SingleRecipeInput) =
        getRemainingItems(input, RandomSource.createNewThreadLocalInstance())

    fun getRemainingItems(input: SingleRecipeInput, level: Level) = getRemainingItems(input, level.random)

    fun getRemainingItems(input: SingleRecipeInput, random: RandomSource): NonNullList<ItemStack> {
        val list = NonNullList.createWithCapacity<ItemStack>(extraOutputs.size)
        for (extra in extraOutputs) {
            if (extra.probability >= 100) list.add(extra.item.copy())
            else if (extra.probability <= 0) continue
            else if (random.nextIntBetweenInclusive(0, 99) < extra.probability) list.add(extra.item.copy())
        }
        return list
    }

    override fun getSerializer(): RecipeSerializer<*> = ModRecipes.SIFTING.serializer
    override fun getType(): RecipeType<*> = ModRecipes.SIFTING.type

    object Serializer : RecipeSerializer<SiftingRecipe> {
        val CODEC: MapCodec<SiftingRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SiftingRecipe::ingredient),
                ItemStack.OPTIONAL_CODEC.lenientOptionalFieldOf("guaranteedItem").xmap(
                    { opt -> opt.getOrDefault(ItemStack.EMPTY) }, Optional<ItemStack>::of
                ).forGetter(SiftingRecipe::guaranteedItem),
                ChanceItemRecipeOutput.LIST_CODEC.fieldOf("extraOutputs").forGetter(SiftingRecipe::extraOutputs),
                Codec.INT.fieldOf("processingTime").forGetter(SiftingRecipe::processingTime),
                Codec.INT.fieldOf("energyUsage").forGetter(SiftingRecipe::energyUsage)
            ).apply(it, ::SiftingRecipe)
        }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SiftingRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SiftingRecipe::ingredient,
            ItemStack.OPTIONAL_STREAM_CODEC,
            SiftingRecipe::guaranteedItem,
            ChanceItemRecipeOutput.LIST_STREAM_CODEC,
            SiftingRecipe::extraOutputs,
            ByteBufCodecs.INT,
            SiftingRecipe::processingTime,
            ByteBufCodecs.INT,
            SiftingRecipe::energyUsage,
            ::SiftingRecipe
        )

        override fun codec() = CODEC
        override fun streamCodec() = STREAM_CODEC
    }
}