package redcrafter07.processed.materials

interface MaterialContainer {
    val material: Material
    fun getColor(tintIndex: Int): Int? = material.info.color
}