package redcrafter07.processed.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class RocketEntity(entityType: EntityType<RocketEntity>, level: Level) : Entity(entityType, level) {
    companion object {
        val HAS_STANDS: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(RocketEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
        isNoGravity = true
        noPhysics = true
        isInvulnerable = true
    }

    var hasStands = true

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(HAS_STANDS, hasStands)
    }


    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        if (key == HAS_STANDS) hasStands = this.entityData.get(HAS_STANDS)

        super.onSyncedDataUpdated(key)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.contains("hasStands", Tag.TAG_BYTE.toInt())) {
            hasStands = tag.getByte("hasStands") != 0.toByte()
        }
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        tag.putByte("hasStands", if (hasStands) 1.toByte() else 0.toByte())
    }
}