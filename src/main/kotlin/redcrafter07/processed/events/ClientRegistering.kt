package redcrafter07.processed.events

import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.FluidTankEntityRenderer
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.entity.ModEntities
import redcrafter07.processed.entity.RocketEntityModel
import redcrafter07.processed.entity.RocketEntityRenderer
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.gui.*
import redcrafter07.processed.rl
import redcrafter07.processed.transmitters.TransmitterModelLoader

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientRegistering {
    @SubscribeEvent
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), ::GenericMachineMenuScreen)
        event.register(ModMenuTypes.SIFTER_MENU.get(), ::GenericMachineMenuScreen)
        event.register(ModMenuTypes.CRUSHER_MENU.get(), ::GenericMachineMenuScreen)
        event.register(ModMenuTypes.PURIFIER_MENU.get(), ::GenericMachineMenuScreen)
        event.register(ModMenuTypes.WASHER_MENU.get(), ::GenericMachineMenuScreen)
        event.register(ModMenuTypes.LAUNCH_CONTROLLER_MENU.get(), ::LaunchControllerMenuScreen)
        event.register(ModMenuTypes.ITEM_HATCH_MENU.get(), ::DynamicContainerScreen)
        event.register(ModMenuTypes.FLUID_HATCH_MENU.get(), ::FluidHatchScreen)
    }

    @SubscribeEvent
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(ModTileEntities.FLUID_TANK.get()) { FluidTankEntityRenderer }
        event.registerEntityRenderer(ModEntities.ROCKET.get(), ::RocketEntityRenderer)
    }

    @SubscribeEvent
    fun registerLayerDefinitions(e: EntityRenderersEvent.RegisterLayerDefinitions) {
        e.registerLayerDefinition(RocketEntityModel.LAYER_LOCATION, RocketEntityModel::createBodyLayer)
    }

    @SubscribeEvent
    fun registerModelLoaders(e: ModelEvent.RegisterGeometryLoaders) = e.register(rl("transmitter"), TransmitterModelLoader)

    @SubscribeEvent
    fun registerClientExtensions(e: RegisterClientExtensionsEvent) {
        // Registers client extensions for fluids. This gives it the flowing texture, still texture, tint color, and overlay texture.
        // This is required for rendering the fluids.
        class FluidExtension(val tint: Int) : IClientFluidTypeExtensions {
            override fun getTintColor() = tint
            override fun getFlowingTexture() = ResourceLocation.withDefaultNamespace("block/water_flow")
            override fun getStillTexture() = ResourceLocation.withDefaultNamespace("block/water_still")
            override fun getOverlayTexture() = ResourceLocation.withDefaultNamespace("block/water_overlay")
        }

        for (fluid in ModFluids.REGISTERED_FLUIDS) e.registerFluidType(FluidExtension(fluid.color), fluid.type.get())
    }
}