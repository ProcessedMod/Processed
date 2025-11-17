package redcrafter07.processed.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth

open class FireParticle(
    level: ClientLevel, x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double
) : RisingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
    init {
        lifetime = this.random.nextInt(3)
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE

    override fun move(x: Double, y: Double, z: Double) {
        this.boundingBox = this.boundingBox.move(x, y, z)
        this.setLocationFromBoundingbox()
    }

    override fun getQuadSize(scaleFactor: Float): Float {
        val f = (this.age.toFloat() + scaleFactor) / this.lifetime.toFloat()
        return this.quadSize * (1.0f - f * f * 0.5f)
    }

    public override fun getLightColor(partialTick: Float): Int {
        var f = (this.age.toFloat() + partialTick) / this.lifetime.toFloat()
        f = Mth.clamp(f, 0.0f, 1.0f)
        val i = super.getLightColor(partialTick)
        var j = i and 255
        val k = i shr 16 and 255
        j += (f * 15.0f * 16.0f).toInt()
        if (j > 240) {
            j = 240
        }

        return j or (k shl 16)
    }

    class FireParticleProvider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ) = FireParticle(level, x, y, z, xSpeed, ySpeed, zSpeed).apply { pickSprite(sprites) }
    }
}
