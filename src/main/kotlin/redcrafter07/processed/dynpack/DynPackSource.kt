package redcrafter07.processed.dynpack

import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.BuiltInPackSource
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.RepositorySource
import java.util.function.Consumer

object DynPackSource : RepositorySource {
    override fun loadPacks(consumer: Consumer<Pack>) {
        val supplier = BuiltInPackSource.fixedResources(DynPackResources)
        val selection = PackSelectionConfig(true, Pack.Position.TOP, false)
        val pack = Pack.readMetaAndCreate(DynPackResources.location, supplier, PackType.CLIENT_RESOURCES, selection) ?: return
        consumer.accept(pack)
    }
}