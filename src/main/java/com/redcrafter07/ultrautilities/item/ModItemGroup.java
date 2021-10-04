package com.redcrafter07.ultrautilities.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {
    public static final ItemGroup UU_GROUP = new ItemGroup("ultraUtilitiesModTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.PROCESSOR_CORE.get());
        }
    };

    public static final ItemGroup UU_TOOLS = new ItemGroup("ultraUtilitiesModTabTools") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.PROCESSOR_SWORD.get());
        }
    };
}
