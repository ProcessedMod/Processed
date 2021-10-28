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

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
