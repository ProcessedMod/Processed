package redcrafter07.processed.events

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.DataPackRegistryEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.covers.Cover
import redcrafter07.processed.dynpack.DynPackSource
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.integration.theoneprobe.TheOneProbeIntegration
import redcrafter07.processed.miner.MinerData
import redcrafter07.processed.miner.Planetoid
import redcrafter07.processed.network.*
import redcrafter07.processed.particles.FireParticle.FireParticleProvider
import redcrafter07.processed.particles.ModParticles
import redcrafter07.processed.particles.SmokeParticle.SmokeParticleProvider
import java.util.*

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object Registering {
    /** Checks if the block entity associated with this block implements the clazz. */
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

    /** Checks if the block entity associated with this block implements ItemCapableBlockEntity */
    private fun isItemCapable(block: Block): Boolean {
        return blockEntityIs(block, ItemCapableBlockEntity::class.java)
    }

    /** Checks if the block entity associated with this block implements FluidCapableBlockEntity */
    private fun isFluidCapable(block: Block): Boolean {
        return blockEntityIs(block, FluidCapableBlockEntity::class.java)
    }

    /** Checks if the block entity associated with this block implements EnergyCapableBlockEntity */
    private fun isEnergyCapable(block: Block): Boolean {
        return blockEntityIs(block, EnergyCapableBlockEntity::class.java)
    }

    /** Filters out any blocks that don't implement ItemCapableBlockEntity */
    private fun itemCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isItemCapable).toArray { arrayOfNulls(it) }

    /** Filters out any blocks that don't implement FluidCapableBlockEntity */
    private fun fluidCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isFluidCapable).toArray { arrayOfNulls(it) }

    /** Filters out any blocks that don't implement EnergyCapableBlockEntity */
    private fun energyCapable(block: Array<Block>): Array<Block> =
        Arrays.stream(block).filter(Registering::isEnergyCapable).toArray { arrayOfNulls(it) }

    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        // Gets all processed blocks
        val blocks = ModTileEntities.BLOCK_TYPES.entries.stream().flatMap { it.get().validBlocks.stream() }.toList()
            .toTypedArray()

        // registers all blocks that have an item capability as having an item capability
        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is ItemCapableBlockEntity) {
                    if (side == null) blockEntity.itemCapabilityForSide(null, state)
                    else blockEntity.itemCapabilityForSide(BlockSide.translateDirection(side, state), state)
                } else null
            },
            *itemCapable(blocks),
        )
        // registers all blocks that have a processed energy capability as having an item capability
        event.registerBlock(
            ProcessedPower.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is EnergyCapableBlockEntity) {
                    if (side == null) blockEntity.energyCapabilityForSide(null, state)
                    else blockEntity.energyCapabilityForSide(BlockSide.translateDirection(side, state), state)
                } else null
            },
            *energyCapable(blocks),
        )
        // registers all blocks that have a fluid as having an item capability
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is FluidCapableBlockEntity) {
                    if (side == null) blockEntity.fluidCapabilityForSide(null, state)
                    else blockEntity.fluidCapabilityForSide(BlockSide.translateDirection(side, state), state)
                } else null
            },
            *fluidCapable(blocks),
        )

        event.registerItem(
            Capabilities.FluidHandler.ITEM,
            { item, _ -> FluidBucketWrapper(item) },
            *ModFluids.REGISTERED_FLUIDS.map { it.bucket }.toList().toTypedArray()
        )
    }

    @SubscribeEvent
    // registers all packets
    fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(ProcessedMod.ID)

        registrar.playBidirectional(MenuRPCPacket.TYPE, MenuRPCPacket.CODEC, MenuRPCPacket::handle)
        registrar.playBidirectional(RPCPacket.TYPE, RPCPacket.CODEC, RPCPacket::handle)
    }


    @SubscribeEvent
    fun registerPackSources(event: AddPackFindersEvent) = event.addRepositorySource(DynPackSource)

    @SubscribeEvent
    fun loadComplete(e: FMLLoadCompleteEvent) {
        e.enqueueWork { if (ModList.get().isLoaded("theoneprobe")) TheOneProbeIntegration.init() }
    }

    @SubscribeEvent
    fun registerDatapackRegistries(e: DataPackRegistryEvent.NewRegistry) {
        e.dataPackRegistry(
            Planetoid.REGISTRY_KEY,
            Planetoid.CODEC,
            Planetoid.CODEC,
        )
        e.dataPackRegistry(
            MinerData.Fuel.REGISTRY_KEY,
            MinerData.Fuel.CODEC,
            MinerData.Fuel.CODEC,
        )
    }

    @SubscribeEvent
    fun registerParticleProviders(e: RegisterParticleProvidersEvent) {
        e.registerSpriteSet(ModParticles.SMOKE.get(), ::SmokeParticleProvider)
        e.registerSpriteSet(ModParticles.FIRE.get(), ::FireParticleProvider)
    }

    @SubscribeEvent
    fun registerRegistries(e: NewRegistryEvent) {
        e.register(Cover.REGISTRY)
    }
}