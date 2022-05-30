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
    public static final RegistryObject<AdvancedLightningConcentratorRecipe.Serializer> ADVANCED_LIGHTNING_SERIALIZER = RECIPE_SERIALIZER.register("advanced_lightning", AdvancedLightningConcentratorRecipe.Serializer::new);
    public static final RegistryObject<BlockForgeRecipe.Serializer> BLOCK_FORGE_SERIALIZER = RECIPE_SERIALIZER.register("block_forging", BlockForgeRecipe.Serializer::new);
    public static final RegistryObject<ChargedCraftingTableRecipe.Serializer> CHARGED_CRAFTING_SERIALIZER = RECIPE_SERIALIZER.register("charged_crafting", ChargedCraftingTableRecipe.Serializer::new);

    public static IRecipeType<CraftingStationRecipe> PROCESSOR_RECIPE = new CraftingStationRecipe.ProcessorRecipeType();
    public static IRecipeType<LightningConcentratorRecipe> LIGHTNING_RECIPE = new LightningConcentratorRecipe.LightningRecipeType();
    public static IRecipeType<ProcessorAssemblerRecipe> PROCESSOR_ASSEMBLY_RECIPE = new ProcessorAssemblerRecipe.ProcessorAssemblyRecipeType();
    public static IRecipeType<AdvancedLightningConcentratorRecipe> ADVANCED_LIGHTNING_RECIPE = new AdvancedLightningConcentratorRecipe.AdvancedLightningRecipeType();
    public static IRecipeType<BlockForgeRecipe> BLOCK_FORGE_RECIPE = new BlockForgeRecipe.BlockForgeRecipeType();
    public static IRecipeType<ChargedCraftingTableRecipe> CHARGED_CRAFTING_RECIPE = new ChargedCraftingTableRecipe.ChargedCraftingTableRecipeType();

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);

        Registry.register(Registry.RECIPE_TYPE, CraftingStationRecipe.TYPE_ID, PROCESSOR_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, LightningConcentratorRecipe.TYPE_ID, LIGHTNING_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, ProcessorAssemblerRecipe.TYPE_ID, PROCESSOR_ASSEMBLY_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, AdvancedLightningConcentratorRecipe.TYPE_ID, ADVANCED_LIGHTNING_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, ChargedCraftingTableRecipe.TYPE_ID, CHARGED_CRAFTING_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, BlockForgeRecipe.TYPE_ID, BLOCK_FORGE_RECIPE);
    }
}
