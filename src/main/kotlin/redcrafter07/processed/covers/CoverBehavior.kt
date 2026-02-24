package redcrafter07.processed.covers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.BlockCapability

abstract class CoverBehavior(val position: BlockPos, val level: Level, val direction: Direction, val cover: Cover): MenuProvider {
    open fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {}
    open fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {}

    open fun writeExtraData(buf: RegistryFriendlyByteBuf) {}

    fun save(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        tag.putString("id", cover.location.value.toString())
        saveAdditional(tag, registries)
        return tag
    }

    fun load(tag: CompoundTag, registries: HolderLookup.Provider) {
        val id = tag.getString("id")
        val rl = ResourceLocation.parse(id)
        if (rl != cover.location.value) throw IllegalStateException("id on the cover data does not match the cover's id")
        loadAdditional(tag, registries)
    }

    protected fun <T> getCapability(level: Level, capability: BlockCapability<T, Direction?>): T? =
        getCapability(level, capability, position, direction)

    protected fun <T> getCapability(level: Level, capability: BlockCapability<T, Direction?>, pos: BlockPos, dir: Direction): T? {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") return level.getCapability(capability, pos, dir)
            ?: level.getCapability(capability, pos, null)
    }

    open fun dropItems(): List<ItemStack> = listOf(coverItem.defaultInstance)

    val coverItem: Item get() = BuiltInRegistries.ITEM.get(cover.location.value)

    open fun ticks() = false

    /**
     * @return Returns if this function did any work. if not, this cover will be put to sleep for 1 second (20 ticks).
     */
    protected open fun tick(level: ServerLevel): Boolean = false
    fun wakeup() {
        sleepLeft = 0
    }

    private var sleepLeft = 0
    fun executeTick(level: ServerLevel) {
        if (sleepLeft > 0) sleepLeft--
        else if (!tick(level)) sleepLeft = 20
    }

    fun setDirty() {
        level.getChunk(position).isUnsaved = true
    }
}