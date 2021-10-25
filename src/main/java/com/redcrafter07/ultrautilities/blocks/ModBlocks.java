package com.redcrafter07.ultrautilities.blocks;

import com.redcrafter07.ultrautilities.UltraUtilities;
import com.redcrafter07.ultrautilities.item.ModItemGroup;
import com.redcrafter07.ultrautilities.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, UltraUtilities.MOD_ID);

    public static final RegistryObject<Block> LAB_BLOCK = registerBlock("lab_block", () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(0).harvestTool(ToolType.PICKAXE).setRequiresTool()));
    public static final RegistryObject<Block> OVERLOAD_ORE = registerBlock("overload_ore", () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(3).harvestTool(ToolType.PICKAXE).setRequiresTool()));

//    TILE ENTITIES
    public static final RegistryObject<Block> CRAFTING_STATION = registerBlock("crafting_station", () -> new CraftingStationBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(0).harvestTool(ToolType.PICKAXE).setRequiresTool()));
    public static final RegistryObject<Block> OVERLOAD_STATION = registerBlock("overload_station", () -> new OverloadStationBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(0).harvestTool(ToolType.PICKAXE).setRequiresTool()));
    public static final RegistryObject<Block> LIGHTNING_CONCENTRATOR = registerBlock("lightning_concentrator", () -> new LightningConcentratorBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(0).harvestTool(ToolType.PICKAXE).setRequiresTool()));

    public static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block)  {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);

        registerBlockItem(name, toReturn);

        return toReturn;
    }

    public static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ModItemGroup.UU_GROUP)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }


}
