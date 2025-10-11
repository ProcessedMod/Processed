package redcrafter07.processed.events

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.block.cable.CableModelLoader
import redcrafter07.processed.block.tile_entities.FluidTankBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.dynpack.DynPackSource
import redcrafter07.processed.gui.GenericMachineMenuScreen
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.network.IOChangePacket
import redcrafter07.processed.network.MultiblockDestroyPacket
import redcrafter07.processed.network.WrenchModeChangePacket
import redcrafter07.processed.rl
import java.util.*

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object Registering {
    private fun blockEntityIs(block: Block?, clazz: Class<*>): Boolean {
        if (block is EntityBlock) {
            try {
                val be = block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState())
                return clazz.isInstance(be)
            } catch (_: SecurityException) {
                return false
            }
        }
        return false
    }

    private fun isItemCapable(block: Block): Boolean {
        return blockEntityIs(block, ItemCapableBlockEntity::class.java)
    }

    private fun isFluidCapable(block: Block): Boolean {
        return blockEntityIs(block, FluidCapableBlockEntity::class.java)
    }

    private fun isEnergyCapable(block: Block): Boolean {
        return blockEntityIs(block, EnergyCapableBlockEntity::class.java)
    }

    private fun itemCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isItemCapable).toArray { arrayOfNulls(it) }

    private fun fluidCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isFluidCapable).toArray { arrayOfNulls(it) }

    private fun energyCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isEnergyCapable).toArray { arrayOfNulls(it) }

    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        val blocks = ModTileEntities.BLOCK_TYPES.entries.stream().flatMap { it.get().validBlocks.stream() }.toList()
            .toTypedArray()

        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is ItemCapableBlockEntity) {
                    return@registerBlock if (side == null) blockEntity.itemCapabilityForSide(null, state)
                    else blockEntity.itemCapabilityForSide(BlockSide.translateDirection(side, state), state)
                }
                return@registerBlock null
            },
            *itemCapable(blocks),
        )
        event.registerBlock(
            ProcessedPower.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is EnergyCapableBlockEntity) {
                    return@registerBlock if (side == null) blockEntity.energyCapabilityForSide(null, state)
                    else blockEntity.energyCapabilityForSide(BlockSide.translateDirection(side, state), state)
                }
                return@registerBlock null
            },
            *energyCapable(blocks),
        )
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is FluidCapableBlockEntity) {
                    return@registerBlock if (side == null) blockEntity.fluidCapabilityForSide(null, state)
                    else blockEntity.fluidCapabilityForSide(BlockSide.translateDirection(side, state), state)
                }
                return@registerBlock null
            },
            *fluidCapable(blocks),
        )
    }

    @SubscribeEvent
    fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(ProcessedMod.ID)

        registrar.playToServer(
            WrenchModeChangePacket.TYPE, WrenchModeChangePacket.CODEC, WrenchModeChangePacket::handleServer
        )
        registrar.playToServer(IOChangePacket.TYPE, IOChangePacket.CODEC, IOChangePacket::handleServer)
        registrar.playToClient(
            MultiblockDestroyPacket.TYPE, MultiblockDestroyPacket.CODEC, MultiblockDestroyPacket::handleClient
        )
    }


    @SubscribeEvent
    fun registerMenuScreens(event: RegisterMenuScreensEvent) =
        event.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), ::GenericMachineMenuScreen)

    @SubscribeEvent
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) =
        event.registerBlockEntityRenderer(ModTileEntities.FLUID_TANK.get()) { FluidTankBlockEntity.FluidTankEntityRenderer }

    @SubscribeEvent
    fun registerPackSources(event: AddPackFindersEvent) = event.addRepositorySource(DynPackSource)

    @SubscribeEvent
    fun registerModelLoaders(e: ModelEvent.RegisterGeometryLoaders) = e.register(rl("cable"), CableModelLoader)
}