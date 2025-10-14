package redcrafter07.processed.block.machine_abstractions

enum class RotationType {
    /** Rotatable on the y-axis. Equivalent to furnaces or chests */
    RotatableHorizontal,
    /** Rotatable on the x and y-axis. Equivalent to end rods or shulkers */
    Rotatable,
    /** Not rotatable. Equivalent to a gold block */
    NonRotatable
}