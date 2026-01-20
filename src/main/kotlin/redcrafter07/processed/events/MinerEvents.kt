package redcrafter07.processed.events

import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.miner.LevelMinerData
import redcrafter07.processed.network.RPCFunctions
import java.time.Instant

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME)
object MinerEvents {
    var timeout = 0
    const val MAX_WAIT_TIME =
        10 * 60 // maximum time a miner is going to wait for a launch pad to be fixed if it's broken. After this, it'll just disappear.

    @SubscribeEvent
    fun onTick(e: ServerTickEvent.Pre) {
        if (timeout > 0) {
            timeout--
            return
        }
        if (!LevelMinerData.shouldDoWork(e.server.overworld())) return
        // only handle arriving miners once a second.
        timeout = 20
        val level = e.server.overworld()
        val data = LevelMinerData.getOrDefault(level)
        val now = Instant.now().epochSecond
        for (id in data.arrived()) {
            val minerData = data[id] ?: continue
            val shouldRemove = handleMinersArrived(level, minerData)
            if (shouldRemove || now - minerData.arrivalEpoch > MAX_WAIT_TIME) {
                data.remove(id)
                val controller = level.getBlockEntity(minerData.controllerPos) ?: continue
                if (controller is LaunchControllerBlockEntity) {
                    controller.minerData = null
                    controller.setChanged()
                }
            }
        }
    }

    fun handleMinersArrived(level: ServerLevel, data: LevelMinerData.LaunchedMinerData): Boolean {
        val controller = level.getBlockEntity(data.controllerPos) ?: return false
        if (controller !is LaunchControllerBlockEntity) return false
        if (!controller.isAssembled) return false
        controller.storedResources.compute(data.item) { _, amount ->
            saturatedAdd(amount ?: 0, data.itemAmount.toLong())
        }
        controller.setChanged()

        val x = data.controllerPos.x
        val y = data.controllerPos.y
        // max distance: 10 chunks (160 blocks), max distance^2: 25600
        for (player in level.players()) {
            val x = player.position().x - x
            val y = player.position().y - y
            if (x * x + y * y <= 25600) RPCFunctions.runLaunchControllerAnimation.sendToClient(
                player,
                data.controllerPos,
                false
            )
        }

        return true
    }

    // https://stackoverflow.com/questions/2632392/saturated-addition-of-two-signed-java-long-values#answer-2632501
    fun saturatedAdd(a: Long, b: Long): Long {
        // n + 0 and 0 + n as well as negative + positive won't overflow.
        return if (a == 0.toLong() || b == 0.toLong() || (a > 0 != b > 0)) a + b
        // both pos
        else if (a > 0.toLong()) {
            // if b is less than the difference between the max value and a, adding b would overflow
            if (Long.MAX_VALUE - a < b) Long.MAX_VALUE else a + b
        }
        // both neg
        else {
            // if the minimum value minus a is greater than b, then adding a to b would only result in an overflow.
            // Example with the bounds being [-10;10], and a being -5 and b being -7:
            // -10 - -5 = -5
            // -5 > -7: true, because -5 + -7 = -12 (which is an overflow)
            // with b = -5:
            // -5 > -5: false, because -5 + -5 = -10 (no overflow)
            // with b = -4:
            // -5 > -4: false, because -5 + -4 = -9 (no overflow)
            if (Long.MIN_VALUE - a > b) Long.MIN_VALUE else a + b
        }
    }
}