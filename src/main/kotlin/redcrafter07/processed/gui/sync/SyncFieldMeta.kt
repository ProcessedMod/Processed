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
        fun getMethod(clazz: Class<*>, name: String, vararg compatibleTypes: Class<*>): Method {
            for (method in clazz.declaredMethods) {
                if (method.name != name) continue
                if (method.parameterCount != compatibleTypes.size) continue
                var err = false
                for (i in 0..<method.parameterCount) {
                    if (!method.parameters[i].type.isAssignableFrom(compatibleTypes[i])) {
                        err = true; break
                    }
                }
                if (!err) return method
            }
            throw NoSuchMethodException("${clazz.name}.${name}(${compatibleTypes.joinToString(separator = ", ")})")
        }

        /** Runs a protected or private method */
        fun invokePrivate(method: Method, obj: Any, vararg args: Any): Any? {
            method.isAccessible = true
            try {
                return method.invoke(obj, *args)
            } finally {
                method.isAccessible = false
            }
        }

        fun <Parent, T> of(
            synchronized: MenuSynced,
            field: KotlinUtils.WritableKtField<Parent, T>,
            fieldName: String,
            clazz: Class<T>,
            parent: Class<Parent>,
            parentVal: Parent,
        ): SyncFieldMeta<T, Parent> {
            val syncType = synchronized.type
            val onSynced = notEmpty(synchronized.onSynchronised) { getMethod(parent, it, clazz) }
            val dirtyChecker = notEmpty(synchronized.dirtyChecker) { getMethod(parent, it, clazz) }

            @Suppress("UNCHECKED_CAST") val codec: StreamCodec<RegistryFriendlyByteBuf, T> =
                (if (synchronized.codecGetter.isNotEmpty()) invokePrivate(
                    parent.getDeclaredMethod(synchronized.codecGetter), parent
                )
                else CodecRegistry.get(clazz) ?: getCodec(
                    fieldName, parent, parentVal
                )
                ?: throw Exception("Could not get a codec for field ${parent.name}.${fieldName} of type $clazz")) as StreamCodec<RegistryFriendlyByteBuf, T>

            val copyMethod = notEmpty(synchronized.copyMethod) { parent.getMethod(it, clazz) }
            if (copyMethod != null) assert(copyMethod.returnType == clazz)
            val condition = notEmpty(synchronized.condition) { getMethod(parent, it, clazz) }

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
                    val method = parent.getDeclaredMethod(fn)
                    val v = invokePrivate(method, parentVal as Any)
                    @Suppress("UNCHECKED_CAST") return v as StreamCodec<RegistryFriendlyByteBuf, Any>
                } catch (_: NoSuchMethodException) {
                }
            }
            return null
        }

        private fun <R> notEmpty(v: String, f: (String) -> R) = if (v.isEmpty()) null else f(v)
    }


    fun copy(v: T, registryAccess: RegistryAccess, obj: Parent): T {
        @Suppress("UNCHECKED_CAST") if (copyMethod != null) return invokePrivate(copyMethod, obj as Any, v as Any) as T

        val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.OTHER)
        if (v == null) return v
        codec.encode(buf, v)
        return codec.decode(buf)
    }

    val needsSavedData = dirtyChecker == null
    private fun isDirtyInner(v: T, obj: Parent, saved: T?): Boolean {
        if (dirtyChecker != null) return invokePrivate(dirtyChecker, obj as Any, v as Any) as Boolean

        if (saved == null) return v != null
        else if (v == null) return true
        return v != saved
    }

    fun isDirty(v: T, obj: Parent, saved: T?): Boolean {
        if (condition != null && !(invokePrivate(condition, obj as Any, v as Any) as Boolean)) return false
        return isDirtyInner(v, obj, saved)
    }

    fun notifySync(old: T, obj: Parent) {
        onSynced?.let { invokePrivate(it, obj as Any, old as Any) }
    }
}