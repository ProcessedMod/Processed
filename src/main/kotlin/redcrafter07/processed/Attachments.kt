package redcrafter07.processed

import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import redcrafter07.processed.covers.ClientCoverAttachment
import redcrafter07.processed.covers.CoverAttachment
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import java.util.function.Supplier

object Attachments {
    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ProcessedMod.ID)

    private val DEFERRED_MULTIBLOCK_CHUNK_ATTACHMENT =
        ATTACHMENT_TYPES.register("multiblock_chunk_attachment", Supplier {
            AttachmentType.builder { _ -> (throw IllegalStateException("meow >:c")) as MultiBlockBlockCache }
                .serialize(MultiBlockBlockCache.CODEC).build()
        })
    private val DEFERRED_LEVEL_MINER_DATA = ATTACHMENT_TYPES.register("level_miner_data", Supplier {
        AttachmentType.builder { _ -> LevelMinerData(HashMap()) }.serialize(LevelMinerData.CODEC).build()
    })
    private val DEFERRED_COVERS = ATTACHMENT_TYPES.register("covers", Supplier {
        AttachmentType.builder { _ -> CoverAttachment() }.serialize(CoverAttachment.Serializer).build()
    })
    private val DEFERRED_COVERS_CLIENT = ATTACHMENT_TYPES.register("covers_client", Supplier {
        AttachmentType.builder { _ -> ClientCoverAttachment(hashMapOf()) }.build()
    })

    val MULTIBLOCK_CHUNK_ATTACHMENT: AttachmentType<MultiBlockBlockCache>
        get() = DEFERRED_MULTIBLOCK_CHUNK_ATTACHMENT.get()
    val LEVEL_MINER_DATA: AttachmentType<LevelMinerData>
        get() = DEFERRED_LEVEL_MINER_DATA.get()
    val COVERS: AttachmentType<CoverAttachment>
        get() = DEFERRED_COVERS.get()
    val COVERS_CLIENT: AttachmentType<ClientCoverAttachment> get() = DEFERRED_COVERS_CLIENT.get()
}