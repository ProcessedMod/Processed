package redcrafter07.processed.ponders

import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.element.ElementLink
import net.createmod.ponder.api.element.EntityElement
import net.createmod.ponder.api.level.PonderLevel
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.createmod.ponder.foundation.PonderScene
import net.createmod.ponder.foundation.instruction.PonderInstruction
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.MoverType
import net.minecraft.world.phys.Vec3
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.entity.ModEntities
import redcrafter07.processed.entity.RocketEntity
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.particles.ModParticles
import redcrafter07.processed.rl


object RocketLauncherScene {
    fun add(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        val schematic = rl("rocket_launcher")
        helper.addStoryBoard(ModBlocks.LAUNCH_CONTROLLER.id, schematic, RocketLauncherScene::scene).highlightAllTags()
    }

    private fun scene(builder: SceneBuilder, util: SceneBuildingUtil) {
        builder.title("launch_controller_launch", "Launch controller launch scene")
        builder.showBasePlate()

        builder.world().showSection(util.select().layer(1), Direction.DOWN)
        builder.idle(10)

        builder.world().showSection(util.select().fromTo(1, 2, 1, 3, 2, 3), Direction.DOWN)
        builder.idle(10)

        builder.world().showSection(util.select().fromTo(1, 2, 4, 1, 5, 4), Direction.DOWN)
        builder.idle(10)

        builder.addKeyframe()

        // wait
        builder.idleSeconds(1)
        builder.overlay().showControls(Vec3(3.5, 1.5, .0), Pointing.RIGHT, 35).rightClick()
            .withItem(ModFluids.FUEL.bucket.get().defaultInstance)
        builder.overlay().showText(35).text("insert_fuel").pointAt(Vec3(3.5, 1.5, .0))
        builder.idle(40)

        builder.overlay().showControls(Vec3(.5, 1.5, .0), Pointing.UP, 60).rightClick()
            .withItem(ModItems.LOCATION_SELECTOR.get().defaultInstance)
        builder.overlay().showText(60).text("insert_location").pointAt(Vec3(.5, 1.5, .0))
        builder.idle(70)

        builder.overlay().showControls(Vec3(.5, 1.5, .0), Pointing.UP, 50).rightClick()
            .withItem(ModItems.ASSEMBLED_MINER.get().defaultInstance)
        builder.overlay().showText(50).text("insert_miner").pointAt(Vec3(.5, 1.5, .0))
        builder.idleSeconds(3)

        builder.addKeyframe()

        // FIX: Hardcoding ewwww
        val ent = builder.world().createEntity {
            val ent = ModEntities.ROCKET.get().create(it)!!
            ent.moveTo(2.5, 1.5, 2.5)
            ent.hasStands = true
            ent
        }
        builder.effects().emitParticles(Vec3(1.2, 2.6, 1.2), RocketLauncherScene::spawnWater1, 30f, 120)
        builder.effects().emitParticles(Vec3(1.2, 2.6, 3.8), RocketLauncherScene::spawnWater2, 30f, 120)
        builder.effects().emitParticles(Vec3(3.8, 2.6, 1.2), RocketLauncherScene::spawnWater3, 30f, 120)
        builder.effects().emitParticles(Vec3(3.8, 2.6, 3.8), RocketLauncherScene::spawnWater4, 30f, 120)

        builder.idle(120)

        builder.addKeyframe()

        builder.world().modifyEntity(ent) { if (it is RocketEntity) it.hasStands = false }

        builder.effects().emitParticles(Vec3(1.2, 2.6, 1.2), RocketLauncherScene::spawnWater1, 30f, 40)
        builder.effects().emitParticles(Vec3(1.2, 2.6, 3.8), RocketLauncherScene::spawnWater2, 30f, 40)
        builder.effects().emitParticles(Vec3(3.8, 2.6, 1.2), RocketLauncherScene::spawnWater3, 30f, 40)
        builder.effects().emitParticles(Vec3(3.8, 2.6, 3.8), RocketLauncherScene::spawnWater4, 30f, 40)
        builder.addInstruction(LaunchRocketInstruction(ent))

        builder.idle(50)

        builder.addKeyframe()

        builder.overlay().showControls(Vec3(2.5, 1.5, .0), Pointing.RIGHT, 20).rightClick()
        builder.overlay().showText(60).pointAt(Vec3(2.5, 1.5, .0)).placeNearTarget().text("check_controller")

        builder.idleSeconds(4)

        builder.overlay().showText(80).pointAt(Vec3(1.5, 1.5, .0)).placeNearTarget().text("ore_deposited")
        builder.idleSeconds(4)

        builder.markAsFinished()
    }

    private class LaunchRocketInstruction(val ent: ElementLink<EntityElement>) : PonderInstruction() {
        private var rocketSpeed = .006
        private var tick = 0

        override fun isComplete(): Boolean = tick == 50

        override fun tick(scene: PonderScene) {
            val ent = scene.resolve(ent) ?: return
            rocketSpeed += .005
            if (rocketSpeed > 10.0) rocketSpeed = 10.0
            ent.ifPresent {
                if (it is RocketEntity) {
                    it.move(MoverType.SELF, Vec3(.0, rocketSpeed, .0))
                    spawnParticles(rocketSpeed, it.position(), scene.world)
                }
            }
        }
    }

    private fun spawnParticles(rocketSpeed: Double, rocket: Vec3, level: PonderLevel) {
        val smoke = ModParticles.SMOKE.get()
        val fire = ModParticles.FIRE.get()
        val y = rocket.y + .25
        for (x in -1..0) {
            val xOff = x.toDouble() / 2 + .25 + rocket.x
            for (z in -1..0) {
                val zOff = z.toDouble() / 2 + .25 + rocket.z
                for (i in 0..10) {
                    val xMeow = xOff + (Math.random() - .5)
                    val zMeow = zOff + (Math.random() - .5)
                    val y = y + (Math.random() - .5) * (rocketSpeed + .5) - 1.5

                    val dX = (Math.random() - .5) / rocket.y * 2
                    val dZ = (Math.random() - .5) / rocket.y * 2

                    level.addParticle(smoke, xMeow, y, zMeow, dX, -.5, dZ)
                }
                for (i in 0..60) {
                    val xMeow = xOff + (Math.random() - .5)
                    val zMeow = zOff + (Math.random() - .5)
                    val y = y + (Math.random() - .5) * (rocketSpeed + .5)

                    val dX = (Math.random() - .5) / rocket.y * 4
                    val dZ = (Math.random() - .5) / rocket.y * 4

                    level.addParticle(fire, xMeow, y, zMeow, dX, -.5, dZ)
                }
            }
        }
    }

    private fun spawnWater1(level: PonderLevel, x: Double, y: Double, z: Double) {
        val dX = .3 + (Math.random() - .5) / 2.5
        val dZ = .3 + (Math.random() - .5) / 2.5
        level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, x, y, z, dX, .0, dZ)
    }

    private fun spawnWater2(level: PonderLevel, x: Double, y: Double, z: Double) {
        val dX = .3 + (Math.random() - .5) / 2.5
        val dZ = .3 + (Math.random() - .5) / 2.5
        level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, x, y, z, dX, .0, -dZ)
    }

    private fun spawnWater3(level: PonderLevel, x: Double, y: Double, z: Double) {
        val dX = .3 + (Math.random() - .5) / 2.5
        val dZ = .3 + (Math.random() - .5) / 2.5
        level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, x, y, z, -dX, .0, dZ)
    }

    private fun spawnWater4(level: PonderLevel, x: Double, y: Double, z: Double) {
        val dX = .3 + (Math.random() - .5) / 2.5
        val dZ = .3 + (Math.random() - .5) / 2.5
        level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, x, y, z, -dX, .0, -dZ)
    }
}