package redcrafter07.processed.dynpack

import com.google.gson.JsonElement
import net.minecraft.SharedConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.metadata.MetadataSectionSerializer
import net.minecraft.server.packs.metadata.pack.PackMetadataSection
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.IoSupplier
import redcrafter07.processed.ProcessedMod
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Optional

object DynPackResources : PackResources {
    val location = PackLocationInfo(
        "processed_dyn_pack",
        Component.literal("Processed Dynamic Resource Pack"),
        PackSource.BUILT_IN,
        Optional.empty()
    );
    val domains = mutableSetOf(ProcessedMod.ID, "minecraft", "forge", "c")
    val contents = DynPackContents()

    override fun getRootResource(vararg names: String?): IoSupplier<InputStream>? =
        if (names.size == 1 && names[0] == "pack.png") IoSupplier {
            ProcessedMod::class.java.getResourceAsStream("/logo.png")!!
        } else null

    override fun getResource(
        ty: PackType, location: ResourceLocation
    ): IoSupplier<InputStream>? = if (ty != PackType.CLIENT_RESOURCES) null
    else contents.getResource(location.namespace, location.path)

    override fun listResources(
        ty: PackType, namespace: String, path: String, output: PackResources.ResourceOutput
    ) {
        if (ty != PackType.CLIENT_RESOURCES) return
        contents.listResources(namespace, path, output)
    }

    override fun getNamespaces(p0: PackType): Set<String?> = domains

    override fun <T> getMetadataSection(serializer: MetadataSectionSerializer<T>): T? {
        if (serializer != PackMetadataSection.TYPE) return null;
        val description = Component.literal("Processed dynamic data")
        val version = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
        @Suppress("UNCHECKED_CAST") return PackMetadataSection(description, version, Optional.empty()) as T
    }

    override fun location(): PackLocationInfo = location

    override fun close() {}

    fun addItemModel(item: ResourceLocation, obj: JsonElement) {
        val path = item.withPath("models/item/${item.path}.json")
        val bytes = obj.toString().toByteArray(StandardCharsets.UTF_8)
        contents.write(path, bytes)
    }
    fun addBlockState(block: ResourceLocation, obj: JsonElement) {
        val path = block.withPath("blockstates/${block.path}.json")
        val bytes = obj.toString().toByteArray(StandardCharsets.UTF_8)
        contents.write(path, bytes)
    }
}