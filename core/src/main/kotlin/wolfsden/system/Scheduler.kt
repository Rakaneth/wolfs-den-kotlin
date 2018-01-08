package wolfsden.system

import wolfsden.entity.Effect
import wolfsden.system.GameStore.curEntities
import java.io.Serializable
import kotlin.math.abs

object Scheduler {
    private var paused = false
    var clock = 1

    fun tick() {
        if (!paused) {
            for (creature in curEntities.filter { it.hasTag("creature") }.sortedByDescending { it.stats!!.spd }) {
                creature.ai!!.delay--
                if (creature.ai!!.delay <= 0 && !creature.effectStack!!.loseTurn) {
                    //log(clock, "Scheduler", "$creature acting on tick $clock")
                    when {
                        creature.isPlayer -> pause() //process player shit
                        else -> {
                            creature.ai!!.getBTree().step()
                        }
                    }
                }
                val effsToRemove: MutableList<Effect> = mutableListOf()
                with(creature.effectStack!!) {
                    effects.forEach {
                        it.tick()
                        it.duration--
                        if (it.duration <= 0) effsToRemove.add(it)
                    }
                }
                effsToRemove.forEach {
                    creature.removeEffect(it)
                }
            }
            //TODO: every tick actions
            clock++
        }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }
}