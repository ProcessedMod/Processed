package redcrafter07.processed.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier


object ModRecipes {
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProcessedMod.ID)

    val MINER_FORM_SERIALIZER = registerSerializer("miner_forming") { MinerFormRecipe.Serializer }

    fun <T : RecipeSerializer<*>> registerSerializer(
        key: String, serializer: Supplier<T>
    ): DeferredHolder<RecipeSerializer<*>, T> = RECIPE_SERIALIZERS.register(key, serializer)
}