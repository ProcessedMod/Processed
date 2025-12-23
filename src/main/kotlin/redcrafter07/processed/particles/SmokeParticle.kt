package redcrafter07.processed.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType
import kotlin.math.absoluteValue

class SmokeParticle(
    level: ClientLevel, x: Double, val origY: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double
) : TextureSheetParticle(level, x, origY, z, xSpeed, ySpeed, zSpeed) {
    init {
        scale(5.0f)
        setSize(0.25f, 0.25f)
        setAlpha(.8f)
        lifetime = this.random.nextInt(5) + 5
        gravity = .0003f
        xd = xSpeed
        yd = ySpeed
        zd = zSpeed
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0f) && (y - origY).absoluteValue < 20.0) {
            this.xd += (this.random.nextFloat() / 5000.0f * (if (this.random.nextBoolean()) 1 else -1).toFloat()).toDouble()
            this.zd += (this.random.nextFloat() / 5000.0f * (if (this.random.nextBoolean()) 1 else -1).toFloat()).toDouble()
            this.yd -= this.gravity.toDouble()
            val prev = this.y
            this.move(this.xd, this.yd, this.zd)
            if (this.y == prev) this.remove()
        } else {
            this.remove()
        }
    }

    class SmokeParticleProvider(val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ) = SmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed).apply { pickSprite(sprites) }
    }
}