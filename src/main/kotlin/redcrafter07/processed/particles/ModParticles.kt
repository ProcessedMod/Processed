package redcrafter07.processed.particles

import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier

object ModParticles {
    val PARTICLES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ProcessedMod.ID)

    val SMOKE = register("smoke")
    val FIRE = register("fire")

    fun register(name: String): DeferredHolder<ParticleType<*>, SimpleParticleType> =
        PARTICLES.register(name, Supplier { SimpleParticleType(false) })
}