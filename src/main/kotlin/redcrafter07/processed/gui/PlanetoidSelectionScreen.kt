package redcrafter07.processed.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionHand
import org.joml.Vector2i
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.widgets.PlanetoidWidget
import redcrafter07.processed.miner.Planetoid
import redcrafter07.processed.network.PlanetoidSelectPacket
import redcrafter07.processed.rl
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class PlanetoidSelectionScreen(
    val childMap: Map<ResourceKey<Planetoid>, List<ResourceKey<Planetoid>>>,
    val rootElements: List<ResourceKey<Planetoid>>,
    val registry: Registry<Planetoid>,
    val hand: InteractionHand,
    parent: ResourceKey<Planetoid>?
) : Screen(Component.empty()) {
    var parent = parent
        set(value) {
            if (value != null && childMap.getOrDefault(value, listOf()).size <= 1) return
            field = value
            repositionElements()
        }

    companion object {
        fun fromRegistry(
            registry: Registry<Planetoid>, parent: ResourceKey<Planetoid>?, hand: InteractionHand
        ): PlanetoidSelectionScreen {
            val childMap = HashMap<ResourceKey<Planetoid>, MutableList<ResourceKey<Planetoid>>>()
            val rootElements = ArrayList<ResourceKey<Planetoid>>()

            for (elem in registry.entrySet()) {
                if (elem.value.parent.isEmpty) rootElements.add(elem.key)
                else childMap.getOrPut(elem.value.parent.get(), ::ArrayList).add(elem.key)
                childMap.getOrPut(elem.key, ::ArrayList).add(0, elem.key)
            }

            return PlanetoidSelectionScreen(childMap, rootElements, registry, hand, parent)
        }

        fun fromRegistrySun(registry: Registry<Planetoid>, hand: InteractionHand): PlanetoidSelectionScreen {
            val sunKey = ResourceKey.create(registry.key(), rl("sun"))
            return if (registry.containsKey(sunKey)) fromRegistry(registry, sunKey, hand)
            else fromRegistry(registry, null, hand)
        }
    }

    fun shouldSelect(key: ResourceKey<Planetoid>): Boolean {
        if (parent == key) return true
        val children = childMap[key]
        return children == null || children.size < 2
    }

    fun goUp() {
        val parent = parent ?: return
        this.parent = registry.get(parent.location())?.parent?.getOrNull()
    }

    fun selectPlanetoid(key: ResourceKey<Planetoid>) {
        val planetoid = registry.get(key) ?: return
        if (planetoid.gravity.isEmpty || planetoid.distance.isEmpty) return
        val conn = Minecraft.getInstance().connection ?: return
        conn.send(PlanetoidSelectPacket(key.location(), planetoid.name, hand))
        Minecraft.getInstance().setScreen(null)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderTransparentBackground(guiGraphics)
    }

    override fun init() {
        val parent = parent
        val elements = if (parent == null) rootElements else childMap.getOrElse(parent, ::listOf)
        if (parent != null) addRenderableWidget(Button.builder(Translations.planetoidSelectionScreenGoUp()) { this.goUp() }
            .pos(0, 0).build())

        val offset = Vector2i(0)
        val scaleHoriz: Double = (width - 40) / 2000.toDouble()
        val scaleVert: Double = (height - 40) / 1100.toDouble()

        val scale = min(scaleHoriz, scaleVert)
        for (key in elements) {
            val elem = registry.get(key) ?: continue
            val onPress = if (shouldSelect(key)) this::selectPlanetoid else this::parent::set
            addRenderableWidget(PlanetoidWidget(elem, key, onPress, scale, offset, key == parent))
        }

        super.init()
    }
}