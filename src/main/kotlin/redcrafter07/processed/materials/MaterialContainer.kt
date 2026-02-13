package redcrafter07.processed.materials

import redcrafter07.processed.materials.data.MaterialBase

interface MaterialContainer {
    val material: MaterialBase
    fun getColor(tintIndex: Int): Int = if(tintIndex == 0) material.color else -1
}