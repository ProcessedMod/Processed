package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

object ModLootTableProvider {
    fun create(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider?>): LootTableProvider {
        return LootTableProvider(
            output,
            mutableSetOf(),
            listOf(SubProviderEntry(::ModBlockLootTables, LootContextParamSets.BLOCK)),
            registries
        )
    }
}