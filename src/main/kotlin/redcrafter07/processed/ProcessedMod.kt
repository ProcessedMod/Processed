package redcrafter07.processed

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.covers.covers.ModCovers
import redcrafter07.processed.entity.ModEntities
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.items.ModItemGroup
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.network.RPCFunctions
import redcrafter07.processed.particles.ModParticles
import redcrafter07.processed.recipe.ModRecipes

fun rl(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ProcessedMod.ID, path)

@Mod(ProcessedMod.ID)
class ProcessedMod(bus: IEventBus, modContainer: ModContainer) {
    companion object {
        const val ID = "processed"
        val LOG: Logger = LoggerFactory.getLogger(ID)
    }

    init {
        assert(ID == modContainer.modId)

        ModEntities.ENTITIES.register(bus)
        ModParticles.PARTICLES.register(bus)
        ModBlocks.BLOCKS.register(bus)
        ModCovers.COVERS.register(bus)
        ModItems.ITEMS.register(bus)
        ModItemGroup.CREATIVE_MODE_TABS.register(bus)
        ModTileEntities.BLOCK_TYPES.register(bus)
        ModDataComponents.DATA_COMPONENTS.register(bus)
        ModMenuTypes.MENUS.register(bus)
        Attachments.ATTACHMENT_TYPES.register(bus)
        ModRecipes.RECIPE_SERIALIZERS.register(bus)
        ModRecipes.RECIPE_TYPES.register(bus)
        ModFluids.FLUID_TYPES.register(bus)
        ModFluids.FLUIDS.register(bus)
        RPCFunctions.register(bus)

        LOG.info("Loaded processed :3")
    }
}