package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.items.ModItems

class EnUsLanguageProvider(output: PackOutput) :
    LanguageProvider(output, ProcessedMod.ID, "en_us") {
    override fun addTranslations() {
        // -- Creative Tabs --
        add("item_group.processed.main", "Processed")
        add("item_group.processed.materials", "Processed Materials")

        // -- Items --
        addItem(ModItems.BLITZ_ORB, "Blitz Orb")
        addItem(ModItems.WRENCH, "Wrench")
        addItem(ModItems.SPACE_HULL, "Space Hull")
        addItem(ModItems.SPACE_TANK, "Space Tank")
        addItem(ModItems.SPACE_ENGINE, "Space Engine")
        addItem(ModItems.SPACE_MINER, "Space Miner")
        addItem(ModItems.SPACE_CARGO_BAY, "Space Cargo Bay")
        addItem(ModItems.MASS_SPECTROMETRY_DATA, "Mass Spectrometry Data")
        addItem(ModItems.MATERIAL_ANALYSIS, "Material Analysis Data")

        add("item.processed.fluid_cover", "Fluid Cover")
        add("item.processed.bucket", "%s Bucket")
        add("item.processed.wrench.tooltip", "§7Tweak other blocks from Processed!")
        add("item.processed.blitz_orb.tooltip", "§7A big orb of energy.")

        add("item.processed.wrench.mode", "§7Mode: %d")
        add("item.processed.wrench.mode.config", "§9Config")
        add("item.processed.wrench.mode.upgrade", "§2Upgrade")
        add("item.processed.wrench.mode.rotate", "§3Rotate")

        add("item.processed.location_selector", "Location Selector")
        add("item.processed.location_selector.change_tooltip", "§7Right-click to select a planetoid")
        add("item.processed.location_selector.unbound", "Unbound")
        add("item.processed.location_selector.unbind_hint", "§7Shift + Right-click to clear binding")
        add("item.processed.location_selector.unbound_message", "§aSuccessfully unbound!")
        add("item.processed.location_selector.bound", "§7Bound to: %s")

        add("item.processed.assembled_mining_rocket", "Assembled Mining Rocket")
        add("item.processed.assembled_mining_rocket.components", "§7Components:")
        add("item.processed.assembled_mining_rocket.stats", "§7Rocket stats:")

        add("processed.item.material_dust", "%s Dust")
        add("processed.item.material_dust.small", "Small %s Dust")
        add("processed.item.material_dust.impure", "Dirty %s Dust")
        add("processed.item.material_dust.washed", "Washed %s Dust")
        add("processed.item.material_dust.pure", "Pure %s Dust")
        add("processed.item.material_ingot", "%s Ingot")
        add("processed.item.material_nugget", "%s Nugget")
        add("processed.item.material_raw", "Raw %s")
        add("processed.item.material_ore", "%s Ore")
        add("processed.item.material_cable", "%s Cable")
        add("processed.item.material_transporter", "%s Transporter")
        add("processed.item.material_pipe", "%s Pipe")

        add("item.processed.component.research", "Contains Research from: %s")

        // -- Blocks --
        addBlock(ModBlocks.BLITZ_ORE, "Blitz Ore")
        addBlock(ModBlocks.FLUID_TANK, "Fluid Tank")
        addBlock(ModBlocks.BIG_SMELTER, "Big Smelter")
        addBlock(ModBlocks.LAUNCH_CONTROLLER, "Launch Controller")
        addBlock(ModBlocks.LANDING_PAD, "Landing Pad")
        addBlock(ModBlocks.BASIC_CASING, "Basic Casing")
        addBlock(ModBlocks.ITEM_INPUT_HATCH, "Item Input Hatch")
        addBlock(ModBlocks.ITEM_OUTPUT_HATCH, "Item Output Hatch")
        addBlock(ModBlocks.FLUID_INPUT_HATCH, "Fluid Input Hatch")
        addBlock(ModBlocks.FLUID_OUTPUT_HATCH, "Fluid Output Hatch")
        addBlock(ModBlocks.MAGNET, "Magnet")
        addBlock(ModBlocks.ION_DETECTOR, "Ion Detector")
        addBlock(ModBlocks.REINFORCED_GLASS, "Reinforced Glass")
        addBlock(ModBlocks.HEAT_VENT, "Heat Vent")
        addBlock(ModBlocks.MATERIAL_ANALYSER_CORE, "Materials analyser core")
        addBlock(ModBlocks.COMPUTER, "Computer")
        addBlock(ModBlocks.MASS_SPECTROMETER, "Mass Spectrometer")
        addBlock(ModBlocks.MOLECULAR_ANALYZER, "Molecular Analyzer")
        addBlock(ModBlocks.MOLECULAR_EXTRUDER, "Molecular Extruder")
        addBlock(ModFluids.FUEL.block, "Fuel")

        // Tiered Blocks
        add("block.processed.creative_power_source", "%d Creative Power Source")
        add("block.processed.energy_hatch", "%s Energy Hatch")
        add("block.processed.computation_hatch", "%s Computation Hatch")
        add("block.processed.powered_furnace", "%s Powered Furnace")
        add("block.processed.sifter", "%s Sifter")
        add("block.processed.crusher", "%s Crusher")
        add("block.processed.purifier", "%s Purifier")
        add("block.processed.washer", "%s Washer")

        // Tooltips
        add("block.processed.fluid_tank.tooltip", "§7Can store fluids, up to 80 Buckets")
        add("block.processed.powered_furnace.tooltip", "§7Smelts items using Power.")
        add("block.processed.sifter.tooltip", "§7Sifts crushed items to extract valuable materials from them")
        add("block.processed.crusher.tooltip", "§7Crushes materials for further processing")
        add("block.processed.purifier.tooltip", "§7Purifies materials for much larger yields")
        add("block.processed.washer.tooltip", "§7Washes materials to help extract resources")
        add("block.processed.big_smelter.tooltip", "§7An arc furnace lol")
        add("block.processed.launch_controller.tooltip", "Launches mining rockets")
        add("block.processed.landing_pad.tooltip", "Meow!")
        add("block.processed.cable.tooltip", "§7Cable Tier: %s. Transfers up to %s per tick")
        add("block.processed.transporter.tooltip", "§7Transfers up to %s per tick")
        add("block.processed.pipe.tooltip", "§7Transfers up to %s per tick")
        add("block.processed.blitz_ore.tooltip", "§7An ore which you can get Blitz Orbs from.")
        add("block.processed.creative_power_source.tooltip", "§7Generates unlimited power for the given tier!")
        add("block.processed.basic_casing.tooltip", "§7The base casing for Processed machines!")
        add("block.processed.energy_hatch.tooltip", "§7Stores %s")
        add("block.processed.computation_hatch.tooltip", "§7Stores %s")
        add("block.processed.item_input_hatch.tooltip", "§7Item Input for a multiblock. Slots: §e4")
        add("block.processed.item_output_hatch.tooltip", "§7Item Output for a multiblock. Slots: §e4")
        add("block.processed.fluid_input_hatch.tooltip", "§7Fluid Input for a multiblock. Space: §a4 B")
        add("block.processed.fluid_output_hatch.tooltip", "§7Fluid Output for a multiblock. Space: §a4 B")
        add("block.processed.magnet.tooltip", "§7Traps ions with the magic of magnets")
        add("block.processed.ion_detector.tooltip", "§7Detects ions")
        add("block.processed.reinforced_glass.tooltip", "§7Glass meant to resist high temperatures and ion beams")
        add("block.processed.heat_vent.tooltip", "§7For venting however much heat u want. seriously. even 15.7 MK")
        add("block.processed.material_analyser_core.tooltip", "§7Analyzes the results of the mass spectrometer")
        add("block.processed.computer.tooltip", "§7Produces 400 Hashes/t while consuming ${ProcessedTier.Advanced.scalePower(8)} FE/t of Advanced Power")
        add("block.processed.mass_spectrometer.tooltip", "§7Uses mass spectrometry to figure out the composition of an element")
        add("block.processed.molecular_analyzer.tooltip", "§7Uses the data of the mass spectrometer to figure out how to turn a raw ore into a usable form")
        add("block.processed.molecular_extruder.tooltip", "§7Uses the instructions produced by the molecular analyzer to turn raw ore into a usable form")

        // Launch Controller State
        add("block.processed.launch_controller.state.idle", "Idle")
        add("block.processed.launch_controller.state.travelling", "Travelling (%s left)")
        add("block.processed.launch_controller.state.mining", "Mining (%s left)")
        add("block.processed.launch_controller.state.travelling_back", "Travelling Back (%s left)")

        // Multiblock State
        add("processed.multiblocks.state.assembled", "Assembled")
        add("processed.multiblocks.state.broken", "Not Assembled")

        // Transmitters
        add("processed.transmitter_state.connected", "§aConnected")
        add("processed.transmitter_state.disallowed", "§bSplit")
        add("processed.transmitter_state.disconnected", "§cDisconnected")
        add("processed.transmitter_state", "§7State: %d")

        add("processed.tiered_machine_info", "§7Max Power: %s§a/t of %s §aPower")

        // -- Miner Attributes --
        add("processed.miner_attribute.mass", "§7Mass: %d kg")
        add("processed.miner_attribute.max_distance", "§7Maximum Distance: %d km")
        add("processed.miner_attribute.tankCapacity", "§7Tank Capacity: %d mB")
        add("processed.miner_attribute.density", "§7Density: %d kg/mB")
        add("processed.miner_attribute.specific_impulse", "§7Specific Impulse (Iₛₚ): %d s")
        add("processed.miner_attribute.thrust", "§7Thrust: %d kN")
        add("processed.miner_attribute.efficiency", "§7Efficiency: %d%%")
        add("processed.miner_attribute.mining_speed", "§7Mining Speed: %d blocks/minute")
        add("processed.miner_attribute.cargo_capacity", "§7Space: %s")
        add("processed.miner_attribute.item_yield", "§7Item yield: %s")
        add("processed.miner_attribute.mining_time", "§7Time spent mining: %s")
        add("processed.miner_attribute.fuel", "§7Fuel: %s")
        add("processed.miner_attribute.stored_fuel", "§7Stored Fuel: %s")

        // -- Gui --
        add("processed.screen.block_config.items", "Configuring Items")
        add("processed.screen.block_config.fluids", "Configuring Fluids")
        add("processed.screen.block_config.upgrades", "Upgrades")
        add("processed.screen.block_config.io", "Input/Output")

        add("processed.io_button.message", "IO Button %d is %s")
        add("processed.io_button.tooltip", "State: %s")
        add("processed.io_state.none", "None")
        add("processed.io_state.input", "Input")
        add("processed.io_state.output", "Output")
        add("processed.io_state.input_output", "Input + Output")
        add("processed.io_state.additional", "Additional Input")
        add("processed.io_state.auxiliary", "Extra Input")

        add("processed.side.top", "Top")
        add("processed.side.bottom", "Bottom")
        add("processed.side.left", "Left")
        add("processed.side.right", "Right")
        add("processed.side.front", "Front")
        add("processed.side.back", "Back")

        add("processed.gui.widget.fluid", "%s: %d")
        add("processed.gui.widget.fluid.capacity", "%s: %d / %d")
        add("processed.gui.widget.energy_bar", "Energy: %s / %s")
        add("processed.gui.widget.energy_bar.normal", "%s FE")
        add("processed.gui.widget.energy_bar.thousand", "%s kFE")
        add("processed.gui.widget.energy_bar.million", "%s MFE")
        add("processed.gui.widget.energy_bar.billion", "%s GFE")
        add("processed.gui.widget.energy_bar.trillion", "%s TFE")
        add("processed.gui.widget.energy_bar.quadrillion", "%s PFE")
        add("processed.gui.widget.energy_bar.quintillion", "%s EFE")
        add("processed.gui.launch_controller_screen.result", "Result: %s")
        add("processed.gui.launch_controller_screen.result_amount", "Amount: %s")
        add("processed.gui.launch_controller_screen.travelling", "Travelling (%s)")
        add("processed.gui.launch_controller_screen.mining", "Mining (%s)")
        add("processed.gui.launch_controller_screen.travelling_back", "Travelling Back (%s)")
        add("processed.gui.launch_controller_screen.stored", "Items Stored:")
        add("processed.gui.launch_controller_screen.stored.infinity", ">10 Items")
        add("processed.gui.launch_controller_screen.fuel", "Fuel: %s/%s")
        add("processed.gui.launch_controller_screen.destination", "Destination: %s")
        add("processed.gui.launch_controller_screen.duration", "Mission Duration: %s")
        add("processed.gui.launch_controller_screen.waiting", "Waiting for a rocket and a bound location selector")

        add("processed.gui.button_push_pull.push", "Pushing")
        add("processed.gui.button_push_pull.pull", "Pulling")
        add("processed.gui.planetoid_selection_screen.go_up", "Go Up")
        add("processed.gui.planetoid_widget.tooltip", "Planetoid: %s")
        add("processed.gui.planetoid.distance", "Distance: %s")
        add("processed.gui.planetoid.gravity", "Gravity (gₒ): %s m/s²")
        add("processed.gui.planetoid.resource", "Contains %s")
        add("processed.gui.progress_tooltip", "Duration: %s")

        // -- Tiers --
        add("processed.tier_0", "Rudimentary")
        add("processed.tier_1", "Basic")
        add("processed.tier_2", "Advanced")
        add("processed.tier_3", "iEnergy Pro Max")
        add("processed.tier_4", "Nuclear")
        add("processed.tier_5", "Quantum")
        add("processed.tier_6", "Void")
        add("processed.tier_7", "Ultimate")

        // -- Units --
        add("processed.unit.kilometers", "%d km")
        add("processed.unit.kilometers.long", "%d Kilometers")
        add("processed.unit.millibuckets", "%d mB")
        add("processed.unit.buckets", "%d B")
        add("processed.unit.items", "%d Items")
        add("processed.unit.stacks", "%d Stacks")
        add("processed.unit.stacks_items", "%d Stacks and %d Items")
        add("processed.unit.crafting_duration.ticks", "%d t")
        add("processed.unit.crafting_duration.secs", "%d s")
        add("processed.unit.crafting_duration.mins", "%d m")

        add("processed.unit.hashes.1024_0", "%d Hashes")
        add("processed.unit.hashes.1024_1", "%d Kilohashes")
        add("processed.unit.hashes.1024_2", "%d Megahashes")
        add("processed.unit.hashes.1024_3", "%d Gigahashes")
        add("processed.unit.hashes.1024_4", "%d Terrahashes")
        add("processed.unit.hashes.1024_5", "%d Petahashes")

        // -- Planetoids --
        add("processed.planetoid.callisto", "Callisto")
        add("processed.planetoid.charon", "Charon")
        add("processed.planetoid.deimos", "Deimos")
        add("processed.planetoid.earth", "Terra")
        add("processed.planetoid.europa", "Europa")
        add("processed.planetoid.ganymede", "Ganymede")
        add("processed.planetoid.hippocamp", "Hippocamp")
        add("processed.planetoid.iapetus", "Iapetus")
        add("processed.planetoid.io", "Io")
        add("processed.planetoid.jupiter", "Jupiter")
        add("processed.planetoid.luna", "Luna")
        add("processed.planetoid.mars", "Mars")
        add("processed.planetoid.mercury", "Mercury")
        add("processed.planetoid.miranda", "Miranda")
        add("processed.planetoid.neptune", "Neptune")
        add("processed.planetoid.phobos", "Phobos")
        add("processed.planetoid.pluto", "Pluto")
        add("processed.planetoid.rhea", "Rhea")
        add("processed.planetoid.saturn", "Saturn")
        add("processed.planetoid.sun", "Sol")
        add("processed.planetoid.thalassa", "Thalassa")
        add("processed.planetoid.titan", "Titan")
        add("processed.planetoid.triton", "Triton")
        add("processed.planetoid.umbriel", "Umbriel")
        add("processed.planetoid.uranus", "Uranus")
        add("processed.planetoid.venus", "Venus")

        // -- Ponders --
        // Launch Controller Ponder
        add("processed.ponder.launch_controller_launch.header", "Launching Space Miners")
        add("processed.ponder.launch_controller_launch.text_1", "Insert fuel for the miner to use")
        add(
            "processed.ponder.launch_controller_launch.text_2",
            "Craft a location selector, select a target, and insert the location selector into the input hatch",
        )
        add(
            "processed.ponder.launch_controller_launch.text_3",
            "Assemble a space miner out of it's component and insert it. It will be consumed.",
        )
        add(
            "processed.ponder.launch_controller_launch.text_4",
            "After launch, check the progress in the gui of the controller",
        )
        add(
            "processed.ponder.launch_controller_launch.text_5",
            "After the miner returned and landed, it will deposit it's resources into the output hatch and any excess will be stored until the output hatch is empty again.",
        )

        // -- Jade --
        add("config.jade.plugin_processed.energy", "Processed Energy")
        add("config.jade.plugin_processed.computations", "Processed Computations")
        add("config.jade.plugin_processed.multiblock_state", "Multiblock State")
        add("config.jade.plugin_processed.crafting_state", "Crafting State")
        add("config.jade.plugin_processed.crafting_state.output", "Output:")

        // -- EMI --
        add("emi.category.processed.sifting", "Sifting")
        add("emi.category.processed.crushing", "Crushing")
        add("emi.category.processed.purifying", "Purifying")
        add("emi.category.processed.washing", "Washing")
        add("emi.category.processed.mass_spectrometry", "Mass Spectrometry")
        add("emi.category.processed.molecular_analysis", "Molecular Analysis")
        add("emi.category.processed.molecular_extruding", "Molecular Extruding")

        // -- Tags --
        add("tag.item.c.small_dusts", "Small Dusts")
        add("tag.item.c.impure_dusts", "Impure Dusts")
        add("tag.item.c.washed_dusts", "Washed Dusts")
        add("tag.item.c.pure_dusts", "Pure Dusts")

        add("tag.fluid.c.fuel", "Fuel")

        TagsLanguageProvider.addTranslations(this,
            "%s Dusts",
            "Small %s Dusts",
            "Dirty %s Dusts",
            "Pure %s Dusts",
            "Washed %s Dusts",
            "%s Ingots",
            "%s Nuggets",
            "%s Ores",
            "Raw %s"
        ) {
            when (it) {
                "aluminium" -> "Aluminium"
                "naquadah" -> "Naquadah"
                "nickel" -> "Nickel"
                "titanium" -> "Titanium"
                "uranium" -> "Uranium"
                "iron" -> "Iron"
                "steel" -> "Steel"
                "nickel_titanium" -> "Nitinol"
                else -> throw IllegalArgumentException("Unsupported Element: $it")
            }
        }
    }
}