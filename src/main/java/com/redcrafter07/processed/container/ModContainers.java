package com.redcrafter07.processed.container;

import com.redcrafter07.processed.Processed;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Processed.MOD_ID);

    public static final RegistryObject<ContainerType<CraftingStationContainer>> CRAFTING_STATION_CONTAINER = CONTAINERS.register("crafting_station_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new CraftingStationContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<OverloadStationContainer>> OVERLOAD_STATION_CONTAINER = CONTAINERS.register("overload_station_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new OverloadStationContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<LightningConcentratorContainer>> LIGHTNING_CONCENTRATOR_CONTAINER = CONTAINERS.register("lightning_concentrator_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new LightningConcentratorContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<ProcessorAssemblerContainer>> PROCESSOR_ASSEMBLER_CONTAINER = CONTAINERS.register("processor_assembler_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new ProcessorAssemblerContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<PowerstoneAccumulatorContainer>> POWERSTONE_ACCUMULATOR_CONTAINER = CONTAINERS.register("powerstone_accumulator_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new PowerstoneAccumulatorContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<AdvancedLightningConcentratorContainer>> ADVANCED_LIGHTNING_CONCENTRATOR_CONTAINER = CONTAINERS.register("advanced_lightning_concentrator_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new AdvancedLightningConcentratorContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<BlockForgeContainer>> BLOCK_FORGE_CONTAINER = CONTAINERS.register("block_forge_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new BlockForgeContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<ChargedCraftingTableContainer>> CHARGED_CRAFTIN_TABLE_CONTAINER = CONTAINERS.register("charged_crafting_table_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos blockPosition = data.readBlockPos();
                World playerWorld = inv.player.getEntityWorld();
                return new ChargedCraftingTableContainer(windowId, playerWorld, blockPosition, inv, inv.player);
            })));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
