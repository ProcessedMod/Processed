package redcrafter07.processed.fluid

import net.minecraft.Util
import net.minecraft.client.color.item.ItemColor
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidType.Properties
import net.neoforged.neoforge.registries.*
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl
import java.util.function.Supplier

object ModFluids {
    val FLUIDS: DeferredRegister<Fluid> = DeferredRegister.create(Registries.FLUID, ProcessedMod.ID)
    val FLUID_TYPES: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ProcessedMod.ID)
    val REGISTERED_FLUIDS =
        ArrayList<RegisteredFluid<FluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, LiquidBlock, TintedBucketItem>>()

    val FUEL = register("fuel", Properties.create(), RenderUtils.color(0xe1, 0xc6, 0x99))

    // Registers a liquid block, fluid type, flowing fluid, still fluid, and a fluid bucket.
    // The color and flowing / still textures are registered in Registering::registerClientExtensions.
    // The bucket color is applied because the `TintedBucketItem` implements `ItemColor` and provides the color that way.
    fun register(
        name: String, properties: Properties, color: Int,
    ): RegisteredFluid<FluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, LiquidBlock, TintedBucketItem> {
        val type = FLUID_TYPES.register(
            name, Supplier { FluidType(properties.descriptionId(Util.makeDescriptionId("block", rl(name)))) })

        val props = BaseFlowingFluid.Properties(
            type,
            DeferredHolder.create(Registries.FLUID, rl(name)),
            DeferredHolder.create(Registries.FLUID, rl("flowing_$name"))
        ).block(DeferredBlock.createBlock(rl(name))).bucket(DeferredItem.createItem(rl("${name}_bucket")))

        val still = FLUIDS.register(name, Supplier { BaseFlowingFluid.Source(props) })
        val flowing = FLUIDS.register("flowing_$name", Supplier { BaseFlowingFluid.Flowing(props) })
        val block = ModBlocks.BLOCKS.register(name, Supplier {
            LiquidBlock(
                still.get(),
                BlockBehaviour.Properties.of().noCollission().strength(100f).noLootTable().replaceable().pushReaction(
                    PushReaction.DESTROY
                ).liquid()
            )
        })
        val bucket = ModItems.registerItem("${name}_bucket") { TintedBucketItem(still.get(), color) }
        val registered = RegisteredFluid(type, still, flowing, block, bucket, color)
        REGISTERED_FLUIDS.add(registered)
        return registered
    }

    class TintedBucketItem(
        content: Fluid, val color: Int, properties: Properties = Properties().stacksTo(1).craftRemainder(Items.BUCKET)
    ) : BucketItem(content, properties), ItemColor {
        // we only want to tint the fluid in the bucket, not the bucket itself. The fluid's element has tint index 1, so only tint that.
        // Return -1 otherwise, which is in 2's complement 32-bit signed integer all bits set, meaning 0xFFFFFFFF, equivalent to #ffffffff, which is white.
        override fun getColor(stack: ItemStack, tintIndex: Int): Int = if (tintIndex == 1) color else -1
    }

    data class RegisteredFluid<Type : FluidType, Still : Fluid, Flowing : Fluid, Block : LiquidBlock, Bucket : BucketItem>(
        val type: DeferredHolder<FluidType, Type>,
        val still: DeferredHolder<Fluid, Still>,
        val flowing: DeferredHolder<Fluid, Flowing>,
        val block: DeferredBlock<Block>,
        val bucket: DeferredItem<Bucket>,
        val color: Int,
    )
}