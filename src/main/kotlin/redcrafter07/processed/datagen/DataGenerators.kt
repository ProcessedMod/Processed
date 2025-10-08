package redcrafter07.processed.datagen

import net.minecraft.data.DataGenerator
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import redcrafter07.processed.ProcessedMod

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun gatherData(ev: GatherDataEvent) {
        val generator: DataGenerator = ev.generator
        val packOutput = generator.packOutput
        val lookupProvider = ev.lookupProvider
        val existingFileHelper: ExistingFileHelper = ev.existingFileHelper

        generator.addProvider(ev.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider))
        generator.addProvider(ev.includeServer(), ModWorldGenProvider(packOutput, lookupProvider))

        generator.addProvider(ev.includeClient(), ModBlockStateProvider(packOutput, existingFileHelper))
        generator.addProvider(ev.includeClient(), ModItemModelProvider(packOutput, existingFileHelper))

        val blockTagGenerator = generator.addProvider(
            ev.includeServer(), ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper)
        )
        generator.addProvider(
            ev.includeServer(),
            ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper)
        )

    }
}