package redcrafter07.processed.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.rl
import java.util.function.Supplier


object ModRecipes {
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProcessedMod.ID)
    val RECIPE_TYPES: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, ProcessedMod.ID)

    val MINER_FORM_SERIALIZER = registerSerializer("miner_forming") { MinerFormRecipe.Serializer }

    val SIFTING = registerRecipe("sifting") { SiftingRecipe.Serializer }
    val CRUSHING = registerRecipe("crushing") { CrushingRecipe.Serializer }
    val WASHING = registerRecipe("washing") { WashingRecipe.Serializer }
    val PURIFYING = registerRecipe("purifying") { PurifyingRecipe.Serializer }

    fun <T : RecipeSerializer<*>> registerSerializer(
        key: String, serializer: Supplier<T>
    ): DeferredHolder<RecipeSerializer<*>, T> = RECIPE_SERIALIZERS.register(key, serializer)

    fun <R : Recipe<*>, S : RecipeSerializer<R>> registerRecipe(
        name: String, serializerName: String = name, serializer: Supplier<S>
    ): DeferredRecipe<R, S> {
        val loc = rl(name)
        val serializer = registerSerializer(serializerName, serializer)
        val type = RECIPE_TYPES.register(name, Supplier {
            RecipeType.simple<R>(loc)
        })
        return DeferredRecipe(type, serializer)
    }

    class DeferredRecipe<R : Recipe<*>, S : RecipeSerializer<R>>(
        val deferredType: DeferredHolder<RecipeType<*>, RecipeType<R>>,
        val deferredSerializer: DeferredHolder<RecipeSerializer<*>, S>
    ) {
        val type: RecipeType<R> get() = deferredType.get()
        val serializer: S get() = deferredSerializer.get()
    }
}