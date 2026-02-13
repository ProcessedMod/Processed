package redcrafter07.processed.materials.data

import net.minecraft.network.chat.MutableComponent
import redcrafter07.processed.Translations

interface MaterialBase {
    val color: Int
    val identifier: String
    val component: MutableComponent get() = Translations.materialName(identifier)

}