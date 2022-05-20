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

public class ChargedCraftingTableRecipe implements IChargedCraftingTableRecipe {

    //Credit: Kaupenjoe | See CREDITS.txt line 69 for more details.

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int energyCost;

    public ChargedCraftingTableRecipe(ResourceLocation id, ItemStack output, int energyCost, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.energyCost = energyCost;
        this.recipeItems = recipeItems;
    }


    @Override
    public boolean matches(Inventory inv, World worldIn) {
        return recipeItems.get(0).test(inv.getStackInSlot(0));
    }

    @Override
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

    public int getEnergyCost() {
        return energyCost;
    }

    public ItemStack getIcon() {
        return new ItemStack(ModBlocks.CHARGED_CRAFTING_TABLE.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADVANCED_LIGHTNING_SERIALIZER.get();
    }

    public static class ChargedCraftingTableRecipeType implements IRecipeType<ChargedCraftingTableRecipe> {
        @Override
        public String toString() {
            return ChargedCraftingTableRecipe.TYPE_ID.toString();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ChargedCraftingTableRecipe> {

        @Override
        public ChargedCraftingTableRecipe read(ResourceLocation recipeId, JsonObject json) {
            ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));

            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            int cost = JSONUtils.getInt(json, "energyCost");
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.deserialize(ingredients.get(i)));
            }

            return new ChargedCraftingTableRecipe(recipeId, output,
                    cost, inputs);
        }

        @Nullable
        @Override
        public ChargedCraftingTableRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.read(buffer));
            }

            ItemStack output = buffer.readItemStack();
            int cost = buffer.readInt();

            return new ChargedCraftingTableRecipe(recipeId, output, cost, inputs);
        }

        @Override
        public void write(PacketBuffer buffer, ChargedCraftingTableRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buffer);
            }
            buffer.writeItemStack(recipe.getRecipeOutput(), false);
        }
    }
}
