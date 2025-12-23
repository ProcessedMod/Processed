package redcrafter07.processed

import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
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

    val MULTIBLOCK_CHUNK_ATTACHMENT: AttachmentType<MultiBlockBlockCache>
        get() = DEFERRED_MULTIBLOCK_CHUNK_ATTACHMENT.get()
    val LEVEL_MINER_DATA: AttachmentType<LevelMinerData>
        get() = DEFERRED_LEVEL_MINER_DATA.get()
}