package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.item.ModItemGroup;
import com.redcrafter07.processed.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Processed.MOD_ID);

    //    NORMAL BLOCKS
    public static final RegistryObject<Block> LAB_BLOCK = registerBlock("lab_block", () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(1).harvestTool(ToolType.PICKAXE).setRequiresTool()));
    public static final RegistryObject<Block> OVERLOAD_ORE = registerBlock("overload_ore", () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(3).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F)));
    public static final RegistryObject<Block> POWERSTONE_PLUG = registerBlock("powerstone_plug", () -> new PowerstonePlugBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(3).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F).notSolid()));
    public static final RegistryObject<Block> OVERLOADED_QUERY = registerBlock("overloaded_query", () -> new OverloadedQueryBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(10, 10)
            .noDrops().tickRandomly()));
    public static final RegistryObject<Block> MACHINE_CASING = registerBlock("machine_casing", () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(3).harvestTool(ToolType.PICKAXE).setRequiresTool().setLightLevel(value -> 5).hardnessAndResistance(1.0F, 6.0F)));

    //    MODIFIED BLOCKS
    public static final RegistryObject<Block> OVERLOAD_BLOCK = registerBlock("overload_block", () -> new OverloadBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(3).harvestTool(ToolType.PICKAXE).setRequiresTool().setLightLevel(value -> 5).hardnessAndResistance(1.0F, 6.0F)));
    public static final RegistryObject<Block> MOTHERBOARD = registerBlock("motherboard", () -> new MotherboardBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(2).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(0.5F, 3.0F).notSolid()));
    public static final RegistryObject<Block> ENVIRONMENTAL_SUPPRESSOR = registerBlock("environmental_suppressor", () -> new EnvironmentalSuppressorBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(1).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.0F, 3.0F).notSolid().setLightLevel(value -> 14)));
    public static final RegistryObject<Block> POWERSTONE_CONVERTER = registerBlock("powerstone_converter", () -> new PowerstoneConverterBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(2).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(0.5F, 3F).notSolid()));
    public static final RegistryObject<Block> POWERSTONE_ACCUMULATOR = registerBlock("powerstone_accumulator", () -> new PowerstoneAccumulatorBlock(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(2).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(0.5F, 3F)));
    public static final RegistryObject<Block> TESLA_COIL = registerBlock("tesla_coil", () -> new TeslaCoilBlock(AbstractBlock.Properties.create(Material.IRON).harvestLevel(2).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(0.5F, 3F)));

    //    TILE ENTITIES
    public static final RegistryObject<Block> OVERLOAD_STATION = registerBlock("overload_station", () -> new OverloadStationBlock(AbstractBlock.Properties.create(Material.ROCK).notSolid().harvestLevel(1).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.0F, 6.0F)));
    public static final RegistryObject<Block> CRAFTING_STATION = registerBlock("crafting_station", () -> new CraftingStationBlock(AbstractBlock.Properties.create(Material.ROCK).notSolid().harvestLevel(1).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.0F, 6.0F)));
    public static final RegistryObject<Block> LIGHTNING_CONCENTRATOR = registerBlock("lightning_concentrator", () -> new LightningConcentratorBlock(AbstractBlock.Properties.create(Material.ROCK).notSolid().harvestLevel(1).harvestTool(ToolType.PICKAXE).setRequiresTool().hardnessAndResistance(1.0F, 6.0F)));
    public static final RegistryObject<Block> PROCESSOR_ASSEMBLER = registerBlock("processor_assembler", () -> new ProcessorAssemblerBlock(AbstractBlock.Properties.create(Material.ROCK).notSolid().harvestLevel(1).harvestTool(ToolType.PICKAXE).notSolid().hardnessAndResistance(1.0F, 6.0F)));
    public static final RegistryObject<Block> POWERSTONE_RECEIVER = registerBlock("powerstone_receiver", () -> new PowerstoneReceiverBlock(AbstractBlock.Properties.from(POWERSTONE_CONVERTER.get())));
    public static final RegistryObject<Block> ADVANCED_LIGHTNING_CONCENTRATOR = registerBlock("advanced_lightning_concentrator", () -> new AdvancedLightningConcentratorBlock(AbstractBlock.Properties.from(LIGHTNING_CONCENTRATOR.get())));
    public static final RegistryObject<Block> CHARGED_CRAFTING_TABLE = registerBlock("charged_crafting_table", () -> new ChargedCraftingTableBlock(AbstractBlock.Properties.from(CRAFTING_STATION.get())));
    public static final RegistryObject<Block> BLOCK_FORGE = registerBlock("block_forge",
            () -> new BlockForgeBlock(AbstractBlock.Properties.create(Material.ROCK).notSolid().setRequiresTool()
                    .harvestLevel(2).harvestTool(ToolType.PICKAXE).hardnessAndResistance(6.0F, 6.0F))
    );

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);

        registerBlockItem(name, toReturn);

        return toReturn;
    }

    public static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }


}
