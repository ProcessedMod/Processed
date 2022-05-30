package com.redcrafter07.processed.item;

import com.redcrafter07.processed.Processed;
import net.minecraft.item.Item;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Processed.MOD_ID);

    public static final RegistryObject<Item> PROCESSOR_SWORD = ITEMS.register("processor_sword", () -> new ProcessorSwordItem(ModItemTier.PROCESSOR, 450, 20f, new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(1).isImmuneToFire()));

    public static final RegistryObject<Item> CRAFTING_PROCESSOR = ITEMS.register("crafting_processor", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> PROCESSOR_CORE = ITEMS.register("processor_core", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> OVERLOAD_PROCESSOR = ITEMS.register("overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> POWERED_OVERLOAD_PROCESSOR = ITEMS.register("powered_overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> ENVIRONMENTAL_PROCESSOR = ITEMS.register("environmental_processor", () -> new EnvironmentalProcessor(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> CHARGED_OVERLOAD_PROCESSOR = ITEMS.register("charged_overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> DEACTIVATED_CHARGED_OVERLOAD_PROCESSOR = ITEMS.register("deactivated_charged_overload_processor", () -> new DeactivatedChargedOverloadProcessor(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> OVERLOAD_INGOT = ITEMS.register("overload_ingot", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> PROCESSING_UNIT = ITEMS.register("processing_unit", () -> new ProcessingUnit(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> OVERLOAD_BATTERY = ITEMS.register("overload_battery", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxDamage(100)));
    public static final RegistryObject<Item> CREATIVE_OVERLOAD_BATTERY = ITEMS.register("creative_overload_battery", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> LIGHTNING_JAR = ITEMS.register("lightning_jar", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> JAR = ITEMS.register("jar", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> FISHI_STAFF = ITEMS.register("fishi_staff", () -> new FishiStaff(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}