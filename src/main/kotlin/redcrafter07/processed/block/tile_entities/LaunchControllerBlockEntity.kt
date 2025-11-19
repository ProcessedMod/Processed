package redcrafter07.processed.block.tile_entities

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock.Companion.STATE_HORIZ_FACING
import redcrafter07.processed.entity.ModEntities
import redcrafter07.processed.entity.RocketEntity
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator
import redcrafter07.processed.particles.ModParticles
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.deepCopy
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

class LaunchControllerBlockEntity(pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(ModTileEntities.LAUNCH_CONTROLLER.get(), pos, blockState) {
    companion object {
        val copper_grates = Part.block(Blocks.COPPER_GRATE).or(Part.block(Blocks.WAXED_COPPER_GRATE))
            .or(Part.block(Blocks.EXPOSED_COPPER_GRATE)).or(Part.block(Blocks.WEATHERED_COPPER_GRATE))
            .or(Part.block(Blocks.OXIDIZED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_EXPOSED_COPPER_GRATE))
            .or(Part.block(Blocks.WAXED_WEATHERED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_OXIDIZED_COPPER_GRATE))

        val validator = SquareMultiblockValidator.Builder(5, 5, 5).addMapping('c', Part.controller())
            .addMapping('o', Part.block(ModBlocks.BASIC_CASING).or(Part.block(ModBlocks.ITEM_INPUT_HATCH)))
            .addMapping('i', Part.block(ModBlocks.BASIC_CASING)).addMapping('p', Part.block(ModBlocks.LANDING_PAD))
            .addMapping('t', copper_grates).addMapping(' ', Part.ignored()).addMapping('x', Part.block(Blocks.AIR))
            .addLayer(
                "ooooo",
                "oiiio",
                "oiiio",
                "oiiio",
                "oocoo",
            ).addLayer(
                "   t ",
                " ppp ",
                " ppp ",
                " ppp ",
                "     ",
            ).addLayer("   t ", " xxx ", " xxx ", " xxx ", "     ")
            .addLayer("   t ", " xxx ", " xxx ", " xxx ", "     ").addLayer("   t ", " xxx ", " xxx ", " xxx ", "     ")
            .build()
    }

    private var animProg = 0
    private var animRocket: RocketEntity? = null
    private var rocketSpeed = .0
    private var landing = false

    override fun validator() = validator

    override val tier = ProcessedTier.Advanced

    override fun getDisplayName() = Translations.launchControllerName()
    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null

    fun spawnRocket(level: ClientLevel, dir: Direction, x: Double, y: Double, z: Double): RocketEntity? {
        val rocket = ModEntities.ROCKET.get().create(level) ?: return null
        rocket.yRot = dir.get2DDataValue().toFloat() * 90f
        rocket.setPos(x, y, z)
        level.addEntity(rocket)
        return rocket
    }

    fun startLaunchAnimation(level: ClientLevel, pos: BlockPos, state: BlockState) {
        val dir = state.getValue(STATE_HORIZ_FACING)
        val spawnPos = pos.relative(dir, -2).offset(0, 1, 0)

        landing = false
        rocketSpeed = .03
        animProg = 160

        val animRocket = animRocket
        if (animRocket != null) animRocket.moveTo(
            spawnPos.x.toDouble() + .5, spawnPos.y.toDouble(), spawnPos.z.toDouble() + .5
        )
        else this.animRocket = spawnRocket(
            level, dir, spawnPos.x.toDouble() + .5, spawnPos.y.toDouble(), spawnPos.z.toDouble() + .5
        )
    }

    fun startLandingAnimation(level: ClientLevel, pos: BlockPos, state: BlockState) {
        val dir = state.getValue(STATE_HORIZ_FACING)
        val spawnPos = pos.relative(dir, -2)

        landing = true
        rocketSpeed = .0
        animProg = -1


        val animRocket = animRocket
        if (animRocket != null) animRocket.moveTo(
            spawnPos.x.toDouble() + .5, 500.0, spawnPos.z.toDouble() + .5
        )
        else this.animRocket = spawnRocket(
            level, dir, spawnPos.x.toDouble() + .5, 500.0, spawnPos.z.toDouble() + .5
        )
    }

    override fun tileTickClient(level: ClientLevel, pos: BlockPos, state: BlockState) {
        val animRocket = animRocket
        if (animRocket != null) animRocket.hasStands = (!landing && animProg >= 40)

        if (!landing && animRocket != null && animProg < 40) {
            if (animRocket.position().y > 500.0) {
                this.animRocket = null
                level.removeEntity(animRocket.id, Entity.RemovalReason.DISCARDED)
            } else {
                rocketSpeed += .025
                if (rocketSpeed > 10.0) rocketSpeed = 10.0
                animRocket.move(MoverType.SELF, Vec3(.0, rocketSpeed, .0))
            }
        }
        if (landing && animRocket != null) {
            val minY = pos.y + 1

            if (animProg > 0) {
                animRocket.hasStands = true
                animProg--
            } else if (animProg == 0) {
                this.animRocket = null
                level.removeEntity(animRocket.id, Entity.RemovalReason.DISCARDED)
            } else if (animRocket.position().y <= minY) {
                animProg = 40
                animRocket.hasStands = true
            } else {
                animRocket.hasStands = false
                val landingDistance = 500 - minY
                val progress = (animRocket.position().y - minY) / landingDistance
                val speed = Mth.lerp(1 - progress, 10.0, 0.025)
                animRocket.move(MoverType.SELF, Vec3(.0, -speed, .0))
            }
        }
        val doParticles = if (landing) animProg < 0 else animProg < 140
        if (animRocket != null && doParticles) {
            val smoke = ModParticles.SMOKE.get()
            val fire = ModParticles.FIRE.get()
            val y = animRocket.position().y
            for (x in -1..0) {
                val xOff = x.toDouble() / 2 + .25 + animRocket.position().x
                for (z in -1..0) {
                    val zOff = z.toDouble() / 2 + .25 + animRocket.position().z
                    for (i in 0..10) {
                        val xMeow = xOff + (Math.random() - .5)
                        val zMeow = zOff + (Math.random() - .5)
                        val y = y + (Math.random() - .5) * (rocketSpeed + .5) - 1.5

                        val dX = (Math.random() - .5) / animRocket.position().y * 2
                        val dZ = (Math.random() - .5) / animRocket.position().y * 2

                        level.addParticle(smoke, true, xMeow, y, zMeow, dX, -.5, dZ)
                    }
                    for (i in 0..60) {
                        val xMeow = xOff + (Math.random() - .5)
                        val zMeow = zOff + (Math.random() - .5)
                        val y = y + (Math.random() - .5) * (rocketSpeed + .5)

                        val dX = (Math.random() - .5) / animRocket.position().y * 4
                        val dZ = (Math.random() - .5) / animRocket.position().y * 4

                        level.addParticle(fire, true, xMeow, y, zMeow, dX, -.5, dZ)
                    }
                }
            }
        }
        if (landing || animProg <= 0) return
        --animProg

        val launchPadCenter = pos.relative(state.getValue(STATE_HORIZ_FACING), -2).offset(0, 1, 0).toVector3d()

        val corner1 = launchPadCenter.deepCopy().sub(.8, -.4, .8)
        val corner2 = launchPadCenter.deepCopy().sub(.8, -.4, -1.8)
        val corner3 = launchPadCenter.deepCopy().sub(-1.8, -.4, .8)
        val corner4 = launchPadCenter.deepCopy().sub(-1.8, -.4, -1.8)

        val water = ParticleTypes.SPLASH

        for (i in 0..animProg.coerceAtMost(160 - animProg).coerceAtMost(30)) {
            val dX = .3 + (Math.random() - .5) / 2.5
            val dZ = .3 + (Math.random() - .5) / 2.5
            level.addParticle(water, true, corner1.x, corner1.y, corner1.z, dX, .0, dZ)
            level.addParticle(water, true, corner2.x, corner2.y, corner2.z, dX, .0, -dZ)
            level.addParticle(water, true, corner3.x, corner3.y, corner3.z, -dX, .0, dZ)
            level.addParticle(water, true, corner4.x, corner4.y, corner4.z, -dX, .0, -dZ)
        }
    }
}