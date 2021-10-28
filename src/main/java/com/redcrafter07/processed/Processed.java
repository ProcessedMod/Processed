package com.redcrafter07.processed;

import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.container.ModContainers;
import com.redcrafter07.processed.data.recipes.ModRecipeTypes;
import com.redcrafter07.processed.item.ModItems;
import com.redcrafter07.processed.screen.CraftingStationScreen;
import com.redcrafter07.processed.screen.LightningConcentratorScreen;
import com.redcrafter07.processed.screen.OverloadStationScreen;
import com.redcrafter07.processed.screen.ProcessorAssemblerScreen;
import com.redcrafter07.processed.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.processing.Processor;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("processed")
public class Processed {
    public static final String MOD_ID = "processed";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Processed() {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus); //Register Items
        ModBlocks.register(eventBus); //Register Blocks
        ModTileEntities.register(eventBus); //Register Tile Entities
        ModContainers.register(eventBus);
        ModRecipeTypes.register(eventBus);

        eventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);

        ScreenManager.registerFactory(ModContainers.CRAFTING_STATION_CONTAINER.get(), CraftingStationScreen::new);
        ScreenManager.registerFactory(ModContainers.OVERLOAD_STATION_CONTAINER.get(), OverloadStationScreen::new);
        ScreenManager.registerFactory(ModContainers.LIGHTNING_CONCENTRATOR_CONTAINER.get(), LightningConcentratorScreen::new);
        ScreenManager.registerFactory(ModContainers.PROCESSOR_ASSEMBLER_CONTAINER.get(), ProcessorAssemblerScreen::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.PROCESSOR_ASSEMBLER.get(), RenderType.getCutout());

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("processed", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
