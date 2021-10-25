package com.redcrafter07.ultrautilities.tileentity;

import com.redcrafter07.ultrautilities.UltraUtilities;
import com.redcrafter07.ultrautilities.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, UltraUtilities.MOD_ID);

    public static RegistryObject<TileEntityType<CraftingStationTile>> CRAFTING_STATION_TILE =
            TILE_ENTITIES.register("crafting_station_tile", () -> TileEntityType.Builder.create(
                    CraftingStationTile::new, ModBlocks.CRAFTING_STATION.get()).build(null));

    public static RegistryObject<TileEntityType<OverloadStationTile>> OVERLOAD_STATION_TILE =
            TILE_ENTITIES.register("overload_station_tile", () -> TileEntityType.Builder.create(
                    OverloadStationTile::new, ModBlocks.OVERLOAD_STATION.get()).build(null));

    public static RegistryObject<TileEntityType<LightningConcentratorTile>> LIGHTNING_CONCENTRATOR_TILE =
            TILE_ENTITIES.register("lightning_concentrator_tile", () -> TileEntityType.Builder.create(
                    LightningConcentratorTile::new, ModBlocks.OVERLOAD_STATION.get()).build(null));


    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
