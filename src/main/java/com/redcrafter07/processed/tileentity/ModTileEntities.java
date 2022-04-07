package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Processed.MOD_ID);

    public static RegistryObject<TileEntityType<CraftingStationTile>> CRAFTING_STATION_TILE =
            TILE_ENTITIES.register("crafting_station_tile", () -> TileEntityType.Builder.create(
                    CraftingStationTile::new, ModBlocks.CRAFTING_STATION.get()).build(null));

    public static RegistryObject<TileEntityType<OverloadStationTile>> OVERLOAD_STATION_TILE =
            TILE_ENTITIES.register("overload_station_tile", () -> TileEntityType.Builder.create(
                    OverloadStationTile::new, ModBlocks.OVERLOAD_STATION.get()).build(null));

    public static RegistryObject<TileEntityType<LightningConcentratorTile>> LIGHTNING_CONCENTRATOR_TILE =
            TILE_ENTITIES.register("lightning_concentrator_tile", () -> TileEntityType.Builder.create(
                    LightningConcentratorTile::new, ModBlocks.LIGHTNING_CONCENTRATOR.get()).build(null));

    public static RegistryObject<TileEntityType<ProcessorAssemblerTile>> PROCESSOR_ASSEMBLER_TILE =
            TILE_ENTITIES.register("processor_assembler_tile", () -> TileEntityType.Builder.create(
                    ProcessorAssemblerTile::new, ModBlocks.PROCESSOR_ASSEMBLER.get()).build(null));

    public static RegistryObject<TileEntityType<PowerstoneConverterTile>> POWERSTONE_CONVERTER_TILE =
            TILE_ENTITIES.register("powerstone_converter_tile", () -> TileEntityType.Builder.create(
                    PowerstoneConverterTile::new, ModBlocks.POWERSTONE_CONVERTER.get()).build(null));


    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
