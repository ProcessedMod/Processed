package com.redcrafter07.processed.item;

import com.redcrafter07.processed.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {
    public static final ItemGroup MAIN_GROUP = new ItemGroup("processedModTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.OVERLOAD_STATION.get());
        }
    };

    public static final ItemGroup TOOLS_GROUP = new ItemGroup("processedModTabTools") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.PROCESSOR_SWORD.get());
        }
    };
}
