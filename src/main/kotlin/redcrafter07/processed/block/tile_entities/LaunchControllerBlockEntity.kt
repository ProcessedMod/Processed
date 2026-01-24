package redcrafter07.processed.block.tile_entities

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.joml.Vector3d
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.entity.ModEntities
import redcrafter07.processed.entity.RocketEntity
import redcrafter07.processed.gui.LaunchControllerMenu
import redcrafter07.processed.items.ModDataComponents
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.miner.MinerCalc
import redcrafter07.processed.miner.MinerData
import redcrafter07.processed.miner.Planetoid
import redcrafter07.processed.multiblock.MultiblockBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator
import redcrafter07.processed.network.RPCFunctions
import redcrafter07.processed.particles.ModParticles
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.deepCopy
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrNull

class LaunchControllerBlockEntity(pos: BlockPos, blockState: BlockState) :
    MultiblockBlockEntity(ModTileEntities.LAUNCH_CONTROLLER.get(), pos, blockState), MenuProvider {
    companion object {
        val copper_grates = Part.block(Blocks.COPPER_GRATE).or(Part.block(Blocks.WAXED_COPPER_GRATE))
            .or(Part.block(Blocks.EXPOSED_COPPER_GRATE)).or(Part.block(Blocks.WEATHERED_COPPER_GRATE))
            .or(Part.block(Blocks.OXIDIZED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_EXPOSED_COPPER_GRATE))
            .or(Part.block(Blocks.WAXED_WEATHERED_COPPER_GRATE)).or(Part.block(Blocks.WAXED_OXIDIZED_COPPER_GRATE))

        val wallBlocks = Part.block(ModBlocks.BASIC_CASING).or(Part.block(ModBlocks.ITEM_INPUT_HATCH).itemInput())
            .or(Part.block(ModBlocks.ITEM_OUTPUT_HATCH).itemOutput())
            .or(Part.block(ModBlocks.FLUID_INPUT_HATCH).fluidInput())
            .or(Part.blocks(ModBlocks.ENERGY_HATCHES.toList()).energyInput())

        val validator =
            SquareMultiblockValidator.Builder(5, 5, 5).addMapping('c', Part.controller()).addMapping('o', wallBlocks)
                .addMapping('i', Part.block(ModBlocks.BASIC_CASING)).addMapping('p', Part.block(ModBlocks.LANDING_PAD))
                .addMapping('t', copper_grates).addMapping(' ', Part.ignored()).addMapping('x', Part.block(Blocks.AIR))
                .addRestriction(Part.blocks(ModBlocks.ENERGY_HATCHES.toList()), 1, 1)
                .addRestriction(Part.block(ModBlocks.ITEM_OUTPUT_HATCH), 1, 1)
                .addRestriction(Part.block(ModBlocks.ITEM_INPUT_HATCH), 1, 1)
                .addRestriction(Part.block(ModBlocks.FLUID_INPUT_HATCH), 1, 1).addLayer(
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
                .addLayer("   t ", " xxx ", " xxx ", " xxx ", "     ")
                .addLayer("   t ", " xxx ", " xxx ", " xxx ", "     ").build()
    }

    val storedResources = HashMap<ResourceLocation, Long>()
    val animator = Animator()
    var minerData: UUID? = null

    var lastLoadedMiner: Pair<ItemStack, Int>? = null
    var lastDestination: Pair<Planetoid, Int>? = null
    var lastResult: MinerCalc.Result? = null

    val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> lastLoadedMiner?.first?.get(ModDataComponents.ASSEMBLED_MINER)?.storedFuel?.amount ?: -1
            1 -> lastResult?.requiredFuel ?: -1
            2 -> lastLoadedMiner?.first?.get(ModDataComponents.CARGO_BAY_DATA)?.capacity ?: 0
            3 -> {
                var stored = 0
                for (resource in storedResources.values) {
                    if (resource >= Int.MAX_VALUE.toLong() || resource + stored.toLong() >= Int.MAX_VALUE.toLong()) {
                        stored = -1
                        break
                    }
                    stored += resource.toInt()
                }
                stored
            }

            else -> 0
        }

        override fun set(index: Int, value: Int) = Unit
        override fun getCount(): Int = 4
    }

    override fun validator() = validator

    override val tier = ProcessedTier.Advanced

    override fun state(): Component? {
        val level = level
        if (level == null || level.isClientSide || level !is ServerLevel) return null
        val minerData = minerData ?: return Translations.launchControllerStateIdle()
        val now = Instant.now().epochSecond
        val minerDataProper = LevelMinerData.get(level, minerData)

        return if (minerDataProper == null) {
            this.minerData = null
            setChanged()
            Translations.launchControllerStateIdle()
        } else if (minerDataProper.miningFinishEpoch <= now) Translations.launchControllerStateTravellingBack(
            minerDataProper.arrivalEpoch - now
        )
        else if (minerDataProper.planetArrivalEpoch <= now) Translations.launchControllerStateMining(minerDataProper.miningFinishEpoch - now)
        else Translations.launchControllerStateTravelling(minerDataProper.planetArrivalEpoch - now)
    }

    fun tryLaunchMiner() {
        val miner = lastLoadedMiner ?: return
        val calc = lastResult ?: return
        val storedFuel = miner.first.get(ModDataComponents.ASSEMBLED_MINER)?.storedFuel ?: return
        if (storedFuel.amount < calc.requiredFuel) return

        val dst = lastDestination ?: return
        if (!dst.first.isTargetable) return
        val input = itemInput() ?: return

        val lvl = level
        if (lvl == null || lvl.isClientSide || lvl !is ServerLevel) return
        val minerDataUUID = minerData
        if (minerDataUUID != null && LevelMinerData.get(lvl, minerDataUUID) != null) return
        this.minerData = null
        val amount = miner.first.get(ModDataComponents.CARGO_BAY_DATA)?.capacity ?: return
        input.handler.items[dst.second].shrink(1)
        input.handler.items[miner.second].shrink(1)
        input.setChanged()

        val now = Instant.now()
        calc.flightTimeOneWay

        // test env
//        val planetArrival = now.plusSeconds(30)
//        val miningFinish = planetArrival.plusSeconds(5)
//        val arrival = miningFinish.plusSeconds(7)

        val planetArrival = now.plusSeconds((60.0 * calc.flightTimeOneWay).toLong())
        val miningFinish = planetArrival.plusSeconds((60.0 * calc.miningTime).toLong())
        val arrival = now.plusSeconds((60.0 * calc.totalTime).toLong())


        val minerData = LevelMinerData.LaunchedMinerData(
            dst.first.resource.get(), amount, arrival, planetArrival, miningFinish, blockPos
        )
        this.minerData = LevelMinerData.put(lvl, minerData)
        setChanged()

        val x = blockPos.x
        val y = blockPos.y
        // max distance: 10 chunks (160 blocks), max distance^2: 25600
        for (player in lvl.players()) {
            val x = player.position().x - x
            val y = player.position().y - y
            if (x * x + y * y <= 25600) RPCFunctions.runLaunchControllerAnimation.sendToClient(player, blockPos, true)
        }
    }

    override fun getDisplayName() = Translations.launchControllerName()
    override fun createMenu(id: Int, inventory: Inventory, p2: Player): AbstractContainerMenu =
        LaunchControllerMenu(id, inventory, this, data)

    override fun tileTickClient(level: ClientLevel, pos: BlockPos, state: BlockState) {
        animator.tick(level, pos, state)
        super.tileTickClient(level, pos, state)
    }

    override fun tileTickServer(level: ServerLevel, pos: BlockPos, state: BlockState) {
        super.tileTickServer(level, pos, state)
        refreshCalcs()
        fuelRocket()
        tryLaunchMiner()

        val output = specialBlock(SpecialBlockType.ItemOutput) ?: return
        val be = level.getBlockEntity(output) ?: return
        if (be !is OutputItemHatchBlockEntity) return

        if (!storedResources.isEmpty()) {
            val forRemoval = mutableSetOf<ResourceLocation>()
            for (entry in storedResources.entries) {
                val item = BuiltInRegistries.ITEM.get(entry.key)
                if (item == Items.AIR) continue
                val stack = item.defaultMaxStackSize
                var left = entry.value

                for (slot in 0..<be.handler.slots) {
                    val toBeInserted = left.toInt().coerceAtMost(stack)
                    val remaining = be.handler.insertItem(slot, ItemStack(item, toBeInserted), false).count
                    left -= toBeInserted - remaining
                }

                if (left == 0.toLong()) forRemoval.add(entry.key)
                entry.setValue(left)
            }
            for (key in forRemoval) storedResources.remove(key)
        }
    }

    fun refreshCalcs() {
        val lvl = level
        val dst = destination()
        val miner = getRocket()
        if (miner == null || lvl == null || dst == null) {
            lastLoadedMiner = null
            lastDestination = null
            lastResult = null
            return
        }
        val planetoidRegistry = lvl.registryAccess().registry(Planetoid.REGISTRY_KEY).getOrNull() ?: return
        val planetoid = planetoidRegistry.get(dst.first)
        if (planetoid == null || !planetoid.isTargetable) {
            lastLoadedMiner = null
            lastDestination = null
            lastResult = null
            return
        }
        if (lastDestination == null || lastResult == null || lastLoadedMiner == null || lastLoadedMiner?.second != miner.second || !ItemStack.isSameItemSameComponents(
                lastLoadedMiner?.first ?: ItemStack.EMPTY, miner.first
            ) || !planetoid.isCalcSame(lastDestination?.first) || dst.second != lastDestination?.second
        ) {
            lastResult = MinerCalc.calculate(miner.first, lvl.registryAccess(), planetoid)
            lastLoadedMiner = if (lastResult != null) miner else null
        }
        if (lastLoadedMiner != null && lastResult != null) lastDestination = Pair(planetoid, dst.second)
    }

    fun getRocket(): Pair<ItemStack, Int>? {
        val lvl = level ?: return null
        if (lvl.isClientSide || lvl !is ServerLevel) return null
        val input = specialBlocks[SpecialBlockType.ItemInput]?.firstOrNull() ?: return null
        val inputHatch = lvl.getBlockEntity(input)
        if (inputHatch !is InputItemHatchBlockEntity) return null
        val items = inputHatch.handler.items
        for (i in 0..<items.size) {
            if (items[i].has(ModDataComponents.ASSEMBLED_MINER)) return Pair(items[i], i)
        }
        return null
    }

    fun destination(): Pair<ResourceLocation, Int>? {
        val lvl = level ?: return null
        if (lvl.isClientSide || lvl !is ServerLevel) return null
        val input = specialBlocks[SpecialBlockType.ItemInput]?.firstOrNull() ?: return null
        val inputHatch = lvl.getBlockEntity(input)
        if (inputHatch !is InputItemHatchBlockEntity) return null
        val items = inputHatch.handler.items
        for (i in 0..<items.size) {
            val v = items[i].get(ModDataComponents.BOUND_PLANETOID) ?: continue
            return Pair(v.location, i)
        }
        return null
    }

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)

        if (!storedResources.isEmpty()) {
            val resources = CompoundTag()
            for (entry in storedResources.entries) if (entry.value > 0.toLong()) resources.putLong(
                entry.key.toString(), entry.value
            )
            tag.put("resources", resources)
        }
        val minerData = minerData
        if (minerData != null) tag.putUUID("minerData", minerData)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        if (tag.contains("resources", Tag.TAG_COMPOUND.toInt())) {
            val resources = tag.getCompound("resources")
            storedResources.clear()
            for (key in resources.allKeys) {
                val amount = resources.getLong(key)
                if (amount > 0.toLong()) storedResources[ResourceLocation.parse(key)] = amount
            }
        }

        if (tag.contains("minerData")) minerData = tag.getUUID("minerData")
    }

    fun itemInput(): InputItemHatchBlockEntity? {
        val lvl = level ?: return null
        if (lvl.isClientSide || lvl !is ServerLevel) return null
        val input = specialBlocks[SpecialBlockType.ItemInput]?.firstOrNull() ?: return null
        return lvl.getBlockEntity(input) as? InputItemHatchBlockEntity
    }

    fun fluidInput(): InputFluidHatchBlockEntity? {
        val lvl = level ?: return null
        if (lvl.isClientSide || lvl !is ServerLevel) return null
        val input = specialBlocks[SpecialBlockType.FluidInput]?.firstOrNull() ?: return null
        return lvl.getBlockEntity(input) as? InputFluidHatchBlockEntity
    }

    fun fuelRocket() {
        val itemInput = itemInput() ?: return
        val rocket = lastLoadedMiner ?: return
        val miner = rocket.first.get(ModDataComponents.ASSEMBLED_MINER) ?: return
        val engine = rocket.first.get(ModDataComponents.ENGINE_DATA) ?: return
        if (!BuiltInRegistries.FLUID.containsKey(engine.fuel)) return
        val fluidInput = fluidInput() ?: return

        val remainingFuel = (lastResult ?: return).requiredFuel - miner.storedFuel.amount
        if (remainingFuel > 0) {
            val fluid = if (miner.storedFuel.isEmpty) FluidStack(
                BuiltInRegistries.FLUID.get(engine.fuel), remainingFuel
            ) else miner.storedFuel.copyWithAmount(remainingFuel)

            val extracted = fluidInput.handler.drain(fluid, IFluidHandler.FluidAction.EXECUTE)
            if (extracted.amount > 0) {
                val fluid = if (miner.storedFuel.isEmpty) extracted else {
                    miner.storedFuel.amount += extracted.amount
                    miner.storedFuel
                }
                val new = MinerData.Assembled(miner.hull, miner.tank, miner.engine, miner.miners, miner.cargoBay, fluid)
                rocket.first.set(ModDataComponents.ASSEMBLED_MINER, new)

                itemInput.handler.setStackInSlot(rocket.second, rocket.first)
                itemInput.setChanged()
            }
        }
    }

    class Animator {
        // launching: none -> spray -> intermediate -> rising -> none
        // landing: none -> landing -> stationary -> none
        enum class Stage {
            None, LaunchSpray, LaunchIntermediate, LaunchRise,

            LandLower, LandStationary;
        }

        class LaunchSprayData(var ticksLeft: Int)
        class LaunchIntermediate(var ticksLeft: Int, var rocketSpeed: Double)
        class LaunchRise(var rocketSpeed: Double)
        class LandStationary(var ticksLeft: Int)

        private var data: Any = Unit
        private var stage: Stage = Stage.None
        private var rocket: RocketEntity? = null

        fun spawnRocket(level: ClientLevel, dir: Direction, x: Double, y: Double, z: Double): RocketEntity? {
            var rocket = this.rocket
            if (rocket != null) {
                rocket.yRot = dir.get2DDataValue().toFloat() * 90f
                rocket.setPos(x, y, z)
                return rocket
            }
            rocket = ModEntities.ROCKET.get().create(level) ?: return null
            rocket.yRot = dir.get2DDataValue().toFloat() * 90f
            rocket.setPos(x, y, z)
            level.addEntity(rocket)
            this.rocket = rocket
            return rocket
        }

        fun startLaunchAnimation(level: ClientLevel, pos: BlockPos, state: BlockState) {
            val dir = state.getValue(BlockProperties.HORIZONTAL_FACING)
            val spawnPos = pos.relative(dir, -2).offset(0, 1, 0)

            spawnRocket(level, dir, spawnPos.x.toDouble() + .5, spawnPos.y.toDouble(), spawnPos.z.toDouble() + .5)
            stage = Stage.LaunchSpray
            data = LaunchSprayData(120)
        }

        fun startLandingAnimation(level: ClientLevel, pos: BlockPos, state: BlockState) {
            val dir = state.getValue(BlockProperties.HORIZONTAL_FACING)
            val spawnPos = pos.relative(dir, -2)
            spawnRocket(level, dir, spawnPos.x.toDouble() + .5, 500.0, spawnPos.z.toDouble() + .5)
            stage = Stage.LandLower
        }

        fun tick(level: ClientLevel, pos: BlockPos, state: BlockState) {
            val animRocket = rocket ?: return
            if (stage == Stage.None) {
                level.removeEntity(animRocket.id, Entity.RemovalReason.DISCARDED)
                this.rocket = null
                return
            }
            animRocket.hasStands = when (stage) {
                Stage.LaunchSpray, Stage.LandStationary -> true
                else -> false
            }

            when (stage) {
                Stage.None -> Unit
                Stage.LaunchSpray -> {
                    val data = data as LaunchSprayData
                    val amount = (120 - data.ticksLeft).coerceAtMost(30)
                    sprayWater(level, launchPadCenter(pos, state), amount)
                    if (data.ticksLeft < 100) rocketParticles(.05, level)

                    data.ticksLeft--
                    if (data.ticksLeft <= 0) {
                        stage = Stage.LaunchIntermediate
                        this.data = LaunchIntermediate(40, .03)
                    }
                }

                Stage.LaunchIntermediate -> {
                    val data = data as LaunchIntermediate
                    val amount = data.ticksLeft.coerceAtMost(30)
                    sprayWater(level, launchPadCenter(pos, state), amount)
                    data.rocketSpeed += .025
                    if (data.rocketSpeed > 10.0) data.rocketSpeed = 10.0
                    animRocket.move(MoverType.SELF, Vec3(.0, data.rocketSpeed, .0))
                    rocketParticles(data.rocketSpeed, level)

                    data.ticksLeft--
                    if (data.ticksLeft <= 0) {
                        stage = Stage.LaunchRise
                        this.data = LaunchRise(data.rocketSpeed)
                    }
                }

                Stage.LaunchRise -> {
                    val data = data as LaunchRise

                    if (animRocket.position().y > 500.0) {
                        this.rocket = null
                        level.removeEntity(animRocket.id, Entity.RemovalReason.DISCARDED)
                        stage = Stage.None
                    } else {
                        data.rocketSpeed += .025
                        if (data.rocketSpeed > 10.0) data.rocketSpeed = 10.0
                        animRocket.move(MoverType.SELF, Vec3(.0, data.rocketSpeed, .0))
                        rocketParticles(data.rocketSpeed, level)
                    }
                }

                Stage.LandLower -> {
                    val minY = pos.y + 1

                    if (animRocket.position().y <= minY.toDouble()) {
                        stage = Stage.LandStationary
                        data = LandStationary(40)
                    } else {
                        val landingDistance = 500 - minY
                        val progress = (animRocket.position().y - minY) / landingDistance
                        val speed = Mth.lerp(1 - progress, 10.0, 0.025)
                        animRocket.move(MoverType.SELF, Vec3(.0, -speed, .0))
                        rocketParticles(speed, level)
                    }
                }

                Stage.LandStationary -> {
                    val data = data as LandStationary
                    data.ticksLeft--
                    if (data.ticksLeft <= 0) {
                        rocket = null
                        level.removeEntity(animRocket.id, Entity.RemovalReason.DISCARDED)
                        stage = Stage.None
                    }
                }
            }
        }

        fun launchPadCenter(pos: BlockPos, state: BlockState) =
            pos.relative(state.getValue(BlockProperties.HORIZONTAL_FACING), -2).offset(0, 1, 0).toVector3d()

        fun sprayWater(level: ClientLevel, launchPadCenter: Vector3d, amount: Int) {
            val water = ParticleTypes.SPLASH

            val corner1 = launchPadCenter.deepCopy().sub(.8, -.4, .8)
            val corner2 = launchPadCenter.deepCopy().sub(.8, -.4, -1.8)
            val corner3 = launchPadCenter.deepCopy().sub(-1.8, -.4, .8)
            val corner4 = launchPadCenter.deepCopy().sub(-1.8, -.4, -1.8)

            for (i in 0..amount) {
                val dX = .3 + (Math.random() - .5) / 2.5
                val dZ = .3 + (Math.random() - .5) / 2.5
                level.addParticle(water, true, corner1.x, corner1.y, corner1.z, dX, .0, dZ)
                level.addParticle(water, true, corner2.x, corner2.y, corner2.z, dX, .0, -dZ)
                level.addParticle(water, true, corner3.x, corner3.y, corner3.z, -dX, .0, dZ)
                level.addParticle(water, true, corner4.x, corner4.y, corner4.z, -dX, .0, -dZ)
            }
        }

        fun rocketParticles(rocketSpeed: Double, level: ClientLevel) {
            val smoke = ModParticles.SMOKE.get()
            val fire = ModParticles.FIRE.get()
            val rocket = this@Animator.rocket ?: return
            val y = rocket.position().y + .25
            for (x in -1..0) {
                val xOff = x.toDouble() / 2 + .25 + rocket.position().x
                for (z in -1..0) {
                    val zOff = z.toDouble() / 2 + .25 + rocket.position().z
                    for (i in 0..10) {
                        val xMeow = xOff + (Math.random() - .5)
                        val zMeow = zOff + (Math.random() - .5)
                        val y = y + (Math.random() - .5) * (rocketSpeed + .5) - 1.5

                        val dX = (Math.random() - .5) / rocket.position().y * 2
                        val dZ = (Math.random() - .5) / rocket.position().y * 2

                        level.addParticle(smoke, true, xMeow, y, zMeow, dX, -.5, dZ)
                    }
                    for (i in 0..60) {
                        val xMeow = xOff + (Math.random() - .5)
                        val zMeow = zOff + (Math.random() - .5)
                        val y = y + (Math.random() - .5) * (rocketSpeed + .5)

                        val dX = (Math.random() - .5) / rocket.position().y * 4
                        val dZ = (Math.random() - .5) / rocket.position().y * 4

                        level.addParticle(fire, true, xMeow, y, zMeow, dX, -.5, dZ)
                    }
                }
            }
        }
    }
}
