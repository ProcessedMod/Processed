package redcrafter07.processed.gui.sync

import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.neoforged.neoforge.network.connection.ConnectionType
import redcrafter07.processed.KotlinUtils
import java.util.HashMap
import java.util.HashSet
import java.util.stream.Stream

class SyncData(val menu: Any) {
    companion object {
        val fieldMapCache: MutableMap<Class<*>, Map<String, SyncFieldMeta<Any, Any>>> = HashMap()
    }

    private val dirty: MutableSet<String> = HashSet()
    private val previousValues: MutableMap<String, Any> = HashMap()
    private val fields: Map<String, SyncFieldMeta<Any, Any>>
    private var secondCooldown: Int = 20

    init {
        val clazz = menu.javaClass
        fields = fieldMapCache.computeIfAbsent(clazz) {
            val potentialFields = KotlinUtils.getKtLikeFields(clazz)
            val fields = HashMap<String, SyncFieldMeta<Any, Any>>()
            for (fieldName in potentialFields) {
                val field = KotlinUtils.getKtFieldAny(clazz, fieldName) ?: continue
                if (field !is KotlinUtils.WritableKtField) continue
                val synchronizedAnnotation = field.annotations.find { it is MenuSynced } ?: continue
                if (synchronizedAnnotation !is MenuSynced) continue
                fields[fieldName] = SyncFieldMeta.of(synchronizedAnnotation, field, fieldName, field.type, clazz, menu)
            }
            fields
        }
    }

    fun markDirty(vararg name: String) {
        dirty.addAll(name)
    }

    fun synchronise(field: String, data: RegistryFriendlyByteBuf) {
        val field = fields[field] ?: return
        val old = field.field.get(menu)
        field.field.set(menu, field.codec.decode(data))
        field.notifySync(old, menu)
    }

    fun dirtyFields(): Stream<String> = dirty.stream()
    fun synchronisedFields(): Stream<String> = fields.keys.stream()

    /**
     * Clears dirty fields and updates previous values
     */
    fun clearDirtyFields(registryAccess: RegistryAccess) {
        for (fieldName in dirty) {
            val field = fields[fieldName] ?: continue
            if (!field.needsSavedData) {
                previousValues.remove(fieldName)
                continue
            }
            previousValues[fieldName] = field.copy(field.field.get(menu), registryAccess, menu)
        }
        dirty.clear()
    }

    fun encodeField(field: String, registryAccess: RegistryAccess): ByteArray? {
        val field = fields[field] ?: return null
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        field.codec.encode(buffer, field.field.get(menu))
        return ByteBufUtil.getBytes(buffer)
    }

    val hasChanges: Boolean get() = dirty.isNotEmpty()

    fun onTick() {
        val shouldSyncSeconds = secondCooldown == 0
        if (shouldSyncSeconds) secondCooldown = 20
        else secondCooldown--

        for (field in fields) {
            if (dirty.contains(field.key)) continue

            val name = field.key
            val field = field.value
            when (field.syncType) {
                SyncType.EveryTick -> dirty.add(name)
                SyncType.EverySecond -> if (shouldSyncSeconds) dirty.add(name)
                SyncType.Manual -> Unit
                SyncType.OnChange -> {
                    val value = field.field.get(menu)
                    if (field.isDirty(value, menu, previousValues[name])) markDirty(name)
                }
            }
        }
    }

    fun detectChanges() {
        for (field in fields) {
            if (dirty.contains(field.key)) continue

            val name = field.key
            val field = field.value
            when (field.syncType) {
                SyncType.EveryTick, SyncType.EverySecond, SyncType.Manual -> Unit

                SyncType.OnChange -> {
                    val value = field.field.get(menu)
                    if (field.isDirty(value, menu, previousValues[name])) markDirty(name)
                }
            }
        }
    }
}