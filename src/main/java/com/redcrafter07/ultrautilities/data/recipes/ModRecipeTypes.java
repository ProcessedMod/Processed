package com.redcrafter07.ultrautilities.data.recipes;

import com.redcrafter07.ultrautilities.UltraUtilities;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, UltraUtilities.MOD_ID);

    public static final RegistryObject<CraftingStationRecipe.Serializer> PROCESSOR_SERIALIZER = RECIPE_SERIALIZER.register("processor", CraftingStationRecipe.Serializer::new);

    public static IRecipeType<CraftingStationRecipe> PROCESSOR_RECIPE = new CraftingStationRecipe.ProcessorRecipeType();

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);

        Registry.register(Registry.RECIPE_TYPE, CraftingStationRecipe.TYPE_ID, PROCESSOR_RECIPE);
    }
}
