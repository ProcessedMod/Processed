package com.redcrafter07.processed.data.recipes;

import com.redcrafter07.processed.Processed;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Processed.MOD_ID);

    public static final RegistryObject<CraftingStationRecipe.Serializer> PROCESSOR_SERIALIZER = RECIPE_SERIALIZER.register("processor", CraftingStationRecipe.Serializer::new);
    public static final RegistryObject<LightningConcentratorRecipe.Serializer> LIGHTNING_SERIALIZER = RECIPE_SERIALIZER.register("lightning", LightningConcentratorRecipe.Serializer::new);
    public static final RegistryObject<ProcessorAssemblerRecipe.Serializer> PROCESSOR_ASSEMBLY_SERIALIZER = RECIPE_SERIALIZER.register("processor_assembly", ProcessorAssemblerRecipe.Serializer::new);

    public static IRecipeType<CraftingStationRecipe> PROCESSOR_RECIPE = new CraftingStationRecipe.ProcessorRecipeType();
    public static IRecipeType<LightningConcentratorRecipe> LIGHTNING_RECIPE = new LightningConcentratorRecipe.LightningRecipeType();
    public static IRecipeType<ProcessorAssemblerRecipe> PROCESSOR_ASSEMBLY_RECIPE = new ProcessorAssemblerRecipe.ProcessorAssemblyRecipeType();

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);

        Registry.register(Registry.RECIPE_TYPE, CraftingStationRecipe.TYPE_ID, PROCESSOR_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, LightningConcentratorRecipe.TYPE_ID, LIGHTNING_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, ProcessorAssemblerRecipe.TYPE_ID, PROCESSOR_ASSEMBLY_RECIPE);
    }
}
