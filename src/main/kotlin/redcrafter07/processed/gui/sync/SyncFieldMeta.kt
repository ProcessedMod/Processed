package redcrafter07.processed.gui.sync

import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.network.connection.ConnectionType
import redcrafter07.processed.KotlinUtils
import redcrafter07.processed.rpc.CodecRegistry
import java.lang.reflect.Method

class SyncFieldMeta<T, Parent>(
    val field: KotlinUtils.WritableKtField<Parent, T>,
    val syncType: SyncType,
    val codec: StreamCodec<RegistryFriendlyByteBuf, T>,

    private val onSynced: Method?,
    private val dirtyChecker: Method?,
    private val copyMethod: Method?,
    private val condition: Method?,
) {
    companion object {
        fun <Parent, T> of(
            synchronized: MenuSynced,
            field: KotlinUtils.WritableKtField<Parent, T>,
            fieldName: String,
            clazz: Class<T>,
            parent: Class<Parent>,
            parentVal: Parent,
        ): SyncFieldMeta<T, Parent> {
            val syncType = synchronized.type
            val onSynced = notEmpty(synchronized.onSynchronised) { parent.getMethod(it, clazz) }
            val dirtyChecker = notEmpty(synchronized.dirtyChecker) { assertBoolReturn(parent.getMethod(it, clazz)) }

            @Suppress("UNCHECKED_CAST") val codec: StreamCodec<RegistryFriendlyByteBuf, T> =
                (if (synchronized.codecGetter.isNotEmpty()) {
                    val codec = parent.getMethod(synchronized.codecGetter).invoke(parent)
                    codec
                } else CodecRegistry.get(clazz) ?: getCodec(
                    fieldName, parent, parentVal
                )) as StreamCodec<RegistryFriendlyByteBuf, T>

            val copyMethod = notEmpty(synchronized.copyMethod) { parent.getMethod(it, clazz) }
            if (copyMethod != null) assert(copyMethod.returnType == clazz)
            val condition = notEmpty(synchronized.condition) { assertBoolReturn(parent.getMethod(it, clazz)) }

            return SyncFieldMeta(field, syncType, codec, onSynced, dirtyChecker, copyMethod, condition)
        }

        private fun <Parent> getCodec(
            field: String, parent: Class<Parent>, parentVal: Parent
        ): StreamCodec<RegistryFriendlyByteBuf, Any>? {
            val fieldName = field[0].lowercase() + field.substring(1)
            val fieldNameCapitalised = field[0].uppercase() + field.substring(1)

            val fns = arrayOf(
                "$fieldName\$codec",
                "${fieldName}Codec",
                "$fieldNameCapitalised\$codec",
                "${fieldNameCapitalised}Codec",
                "get$fieldNameCapitalised\$codec",
                "get${fieldNameCapitalised}Codec"
            )
            for (fn in fns) {
                try {
                    val method = parent.getMethod(fn)
                    @Suppress("UNCHECKED_CAST") return method.invoke(parentVal) as StreamCodec<RegistryFriendlyByteBuf, Any>
                } catch (_: NoSuchMethodException) {
                }
            }
            return null
        }

        private fun assertBoolReturn(method: Method): Method {
            assert(method.returnType == Boolean::class.java || method.returnType == java.lang.Boolean::class.java)
            return method
        }

        private fun <R> notEmpty(v: String, f: (String) -> R) = if (v.isEmpty()) null else f(v)
    }


    fun copy(v: T, registryAccess: RegistryAccess, obj: Parent): T {
        @Suppress("UNCHECKED_CAST") if (copyMethod != null) return copyMethod.invoke(obj, v) as T

        val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        if (v == null) return v
        codec.encode(buf, v)
        return codec.decode(buf)
    }

    val needsSavedData = dirtyChecker == null
    private fun isDirtyInner(v: T, obj: Parent, saved: T?): Boolean {
        if (dirtyChecker != null) return dirtyChecker.invoke(obj, v) as Boolean

        if (saved == null) return v != null
        else if (v == null) return true
        return v != saved
    }

    fun isDirty(v: T, obj: Parent, saved: T?): Boolean {
        if (!isDirtyInner(v, obj, saved)) return false
        if (condition == null) return true
        return condition.invoke(obj, v) as Boolean
    }

    fun notifySync(old: T, obj: Parent) {
        onSynced?.invoke(obj, old)
    }
}