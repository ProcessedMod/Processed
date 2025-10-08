package redcrafter07.processed

import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import redcrafter07.processed.multiblock.MultiBlockCasingCache
import java.util.function.Supplier

object Attachments {
    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ProcessedMod.ID)

    private val DEFERRED_MULTIBLOCK_CHUNK_ATTACHMENT =
        ATTACHMENT_TYPES.register("multiblock_chunk_attachment", Supplier {
            AttachmentType.builder { _ -> MultiBlockCasingCache() }.build()
        })

    val MULTIBLOCK_CHUNK_ATTACHMENT: AttachmentType<MultiBlockCasingCache>
        get() = DEFERRED_MULTIBLOCK_CHUNK_ATTACHMENT.get()
}