package com.redcrafter07.processed.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.redcrafter07.processed.blocks.ModBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ProcessorAssemblerRecipe implements IProcessorAssemblerRecipe {

    //Credit: Kaupenjoe | See CREDITS.txt line 69 for more details.

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public ProcessorAssemblerRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }


    @Override
    public boolean matches(Inventory inv, World worldIn) {
        return recipeItems.get(0).test(inv.getStackInSlot(1)) &&
                recipeItems.get(1).test(inv.getStackInSlot(2)) &&
                recipeItems.get(2).test(inv.getStackInSlot(3)) &&
                recipeItems.get(3).test(inv.getStackInSlot(4));
    }

    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack getCraftingResult(Inventory inv) {
        return output;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
    }

    public ItemStack getIcon() {
        return new ItemStack(ModBlocks.PROCESSOR_ASSEMBLER.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.PROCESSOR_ASSEMBLY_SERIALIZER.get();
    }

    public static class ProcessorAssemblyRecipeType implements IRecipeType<ProcessorAssemblerRecipe> {
        @Override
        public String toString() {
            return ProcessorAssemblerRecipe.TYPE_ID.toString();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ProcessorAssemblerRecipe> {

        @Override
        public ProcessorAssemblerRecipe read(ResourceLocation recipeId, JsonObject json) {
            ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));

            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(4, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.deserialize(ingredients.get(i)));
            }

            return new ProcessorAssemblerRecipe(recipeId, output,
                    inputs);
        }

        @Nullable
        @Override
        public ProcessorAssemblerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(4, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.read(buffer));
            }

            ItemStack output = buffer.readItemStack();

            return new ProcessorAssemblerRecipe(recipeId, output, inputs);
        }

        @Override
        public void write(PacketBuffer buffer, ProcessorAssemblerRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buffer);
            }
            buffer.writeItemStack(recipe.getRecipeOutput(), false);
        }
    }
}
