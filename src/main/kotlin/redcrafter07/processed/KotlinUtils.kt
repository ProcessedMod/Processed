package redcrafter07.processed

import java.lang.reflect.Method
import java.lang.reflect.Modifier

object KotlinUtils {
    fun getKtLikeFields(clazz: Class<*>): Set<String> {
        val set = HashSet<String>()

        // looking for get .. $annotations that's static
        for (method in clazz.methods) {
            if(method.name.startsWith("get") && method.name.endsWith("\$annotations") && Modifier.isStatic(method.modifiers)) {
                val name = method.name[3].lowercase() + method.name.substring(4, method.name.length - 12)
                set.add(name)
            }
        }

        return set
    }

    fun <T> getKtFieldAny(clazz: Class<T>, field: String): KtField<T, Any>? {
        val realName = field[0].uppercase() + field.substring(1)
        val getter = getDeclaredMethodOrNull(clazz, "get$realName") ?: return null
        val setter = clazz.declaredMethods.find { it.name == "set$realName" }
        val annotationHolder = getDeclaredMethodOrNull(clazz, "get$realName\$annotations")
        val annotations = annotationHolder?.annotations ?: arrayOf()

        return if(setter == null) ReadOnlyKtField(getter, annotations)
        else WritableKtField(getter, setter, annotations)
    }

    private fun getDeclaredMethodOrNull(clazz: Class<*>, name: String, vararg parameterTypes: Class<*>): Method? {
        return try {
            clazz.getDeclaredMethod(name, *parameterTypes)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    sealed interface KtField<T, F> {
        fun get(obj: T): F

        /**
         * Tries setting the field. If this is a final field, the field's value won't change and this will return false.
         */
        fun trySet(obj: T, value: F): Boolean = false
        val annotations: Array<Annotation>
        val type: Class<T>
    }

    class WritableKtField<T, F>(private val getter: Method, private val setter: Method, override val annotations: Array<Annotation>): KtField<T, F> {
        @Suppress("UNCHECKED_CAST")
        override fun get(obj: T) = getter.invoke(obj) as F
        @Suppress("UNCHECKED_CAST")
        override val type = getter.returnType as Class<T>
        fun set(obj: T, value: F) {
            setter.invoke(obj, value)
        }

        override fun trySet(obj: T, value: F): Boolean {
            try {
                set(obj, value)
                return true
            } catch (_: Exception) {
                return false
            }
        }
    }

    class ReadOnlyKtField<T, F>(private val getter: Method, override val annotations: Array<Annotation>): KtField<T, F> {
        @Suppress("UNCHECKED_CAST")
        override val type = getter.returnType as Class<T>
        @Suppress("UNCHECKED_CAST")
        override fun get(obj: T) = getter.invoke(obj) as F
    }
}