package redcrafter07.processed.dynpack

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.resources.IoSupplier
import redcrafter07.processed.ProcessedMod
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.HashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DynPackContents {
    class Node private constructor(var data: Any) {
        // Data should only ever be a Map<String, Node> or IoSupplier<InputStream>.
        // This is enforced by making the constructor private and giving 2 functions which take only these arguments to construct Node.
        companion object {
            fun file(contents: IoSupplier<InputStream>): Node = Node(contents);
            fun dir(): Node = Node(HashMap<String, Node>());
        }

        val isLeaf: Boolean get() = data is IoSupplier<*>
        val children: MutableMap<String, Node>
            get() {
                if (data !is MutableMap<*, *>) throw IllegalStateException("Tried to get children of a file")
                @Suppress("UNCHECKED_CAST") return data as MutableMap<String, Node>
            }
        val contents: IoSupplier<InputStream>
            get() {
                if (data !is IoSupplier<*>) throw IllegalStateException("Tried to get children of a file")
                @Suppress("UNCHECKED_CAST") return data as IoSupplier<InputStream>
            }
        val maybeContents: IoSupplier<InputStream>? get() = if (isLeaf) contents else null;

        fun listContents(namespace: String, path: String, output: PackResources.ResourceOutput) {
            if (isLeaf) output.accept(ResourceLocation.fromNamespaceAndPath(namespace, path), contents);
            else for (entry in children.entries) entry.value.listContents(namespace, "$path/${entry.key}", output)
        }

        fun getFile(name: String): Node? = if (isLeaf) null else children[name]

        /**
         * Turns this node into a file, deleting the previous node.
         */
        fun makeIntoFile(contents: IoSupplier<InputStream>) {
            data = contents
        }
    }

    val lock = ReentrantReadWriteLock()
    val root = Node.dir()

    private fun getPath(namespace: String, path: String): Node? {
        var node = root.getFile(namespace) ?: return null;
        for (arg in path.split('/')) node = node.getFile(arg) ?: return null;
        return node;
    }

    fun getResource(namespace: String, path: String): IoSupplier<InputStream>? =
        lock.read { getPath(namespace, path)?.maybeContents }

    fun listResources(namespace: String, path: String, output: PackResources.ResourceOutput) {
        lock.read { getPath(namespace, path)?.listContents(namespace, path, output) }
    }

    fun write(path: ResourceLocation, contents: IoSupplier<InputStream>) {
        lock.write {
            var node = root.children.computeIfAbsent(path.namespace) { Node.dir() }
            for (arg in path.path.split('/')) node = node.children.computeIfAbsent(arg) { Node.dir() }
            node.makeIntoFile(contents)
        }
    }

    fun write(path: ResourceLocation, contents: ByteArray) {
        ProcessedMod.LOG.info("Writing {}/{}: {}", path.namespace, path.path, contents.toString(StandardCharsets.UTF_8))

        write(path) { ByteArrayInputStream(contents) }
    }
}