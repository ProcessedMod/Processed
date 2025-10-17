package redcrafter07.processed.miner

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Registry
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import redcrafter07.processed.rl
import java.util.*

// todo: Better name (planets, moons, and the sun lol)
class Planetoid(
    val name: String,
    val x: Int,
    val y: Int,
    val size: Int,
    val texture: ResourceLocation,
    val parent: Optional<ResourceKey<Planetoid>>,
    val color: Optional<TextColor>,
    val distance: Optional<Long>,
    val gravity: Optional<Float>,
) {
    companion object {
        // The registry key of all planetoids that were registered by a datapack.
        // This is registered in the `DataPackRegistryEvent.NewRegistry` event.
        val REGISTRY_KEY: ResourceKey<Registry<Planetoid>> = ResourceKey.createRegistryKey(rl("planetoids"))

        val CODEC: Codec<Planetoid> = RecordCodecBuilder.create {
            it.group(
                Codec.STRING.fieldOf("name").forGetter(Planetoid::name),
                Codec.INT.fieldOf("x").forGetter(Planetoid::x),
                Codec.INT.fieldOf("y").forGetter(Planetoid::y),
                Codec.INT.fieldOf("size").forGetter(Planetoid::size),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Planetoid::texture),
                ResourceLocation.CODEC.optionalFieldOf("parent").xmap(
                    { rl -> rl.map { rl -> ResourceKey.create(REGISTRY_KEY, rl) } },
                    { opt -> opt.map(ResourceKey<Planetoid>::location) }).forGetter(Planetoid::parent),
                TextColor.CODEC.optionalFieldOf("color").forGetter(Planetoid::color),
                Codec.LONG.optionalFieldOf("distance").forGetter(Planetoid::distance),
                Codec.FLOAT.optionalFieldOf("gravity").forGetter(Planetoid::gravity),
            ).apply(it, ::Planetoid)
        }
    }
}