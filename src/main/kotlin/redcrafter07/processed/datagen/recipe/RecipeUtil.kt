package redcrafter07.processed.datagen.recipe

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient


fun sIng(tag: TagKey<Item>): SizedIngredient = SizedIngredient.of(tag, 1)
fun sIng(fluid: Fluid, amount: Int): SizedFluidIngredient = SizedFluidIngredient.of(fluid, amount)
fun sIng(tag: TagKey<Item>, count: Int): SizedIngredient = SizedIngredient.of(tag, count)
fun s(item: ItemLike): ItemStack = item.asItem().defaultInstance
fun s(item: ItemLike, count: Int) = ItemStack(item.asItem(), count)