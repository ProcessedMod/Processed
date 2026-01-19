package redcrafter07.processed.gui.sync

enum class SyncType {
    EveryTick,
    /** Every 20 ticks */
    EverySecond,
    /** Only when a change happened */
    OnChange,
    /** A field has to manually be marked as dirty using `markDirty("field name")` */
    Manual,
}