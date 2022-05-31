package com.redcrafter07.processed.item;

import com.redcrafter07.processed.Processed;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Processed.MOD_ID);

    // TODO: add functionality
    // public static final RegistryObject<Item> PROCESSOR_SWORD = ITEMS.register("processor_sword", () -> new ProcessorSwordItem(ModItemTier.PROCESSOR, 15f, 1f, new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(1).isImmuneToFire()));

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
    public static final RegistryObject<Item> MODULE_CORE = ITEMS.register("module_core", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(4)));
    public static final RegistryObject<Item> EFFICIENCY_UPGRADE = ITEMS.register("efficiency_upgrade", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(4)));
    public static final RegistryObject<Item> CIRCUIT_BOARD = ITEMS.register("circuit_board", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> ADVANCED_CIRCUIT_BOARD = ITEMS.register("advanced_circuit_board", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> BLADE = ITEMS.register("blade", () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<Item> SAW_BLADE = ITEMS.register("saw_blade", () -> new SawBladeItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP), "saw_blade", 1.5F));
    public static final RegistryObject<Item> CARBON_REINFORCED_SAW_BLADE = ITEMS.register("carbon_reinforced_saw_blade", () -> new SawBladeItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP), "carbon_reinforced_saw_blade", 3F));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}