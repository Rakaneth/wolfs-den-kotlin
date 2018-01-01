package wolfsden.system

import wolfsden.log
import wolfsden.system.GameStore.curEntities

object Scheduler {
    private var paused = false
    var clock = 1

    fun tick() {
        if (!paused) {
            for (creature in curEntities.filter { it.hasTag("creature") }.sortedByDescending { it.stats!!.spd }) {
                creature.ai!!.delay--
                if (creature.ai!!.delay <= 0) {
                    log(clock, "Scheduler", "$creature acting on tick $clock")
                    when {
                        creature.isPlayer -> pause() //process player shit
                        else -> {/*creature.ai!!.getBTree().step()*/
                        }
                    }
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