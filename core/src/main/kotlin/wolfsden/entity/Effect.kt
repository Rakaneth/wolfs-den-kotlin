package wolfsden.entity

import wolfsden.log
import wolfsden.system.GameStore
import wolfsden.system.Scheduler.clock
import java.io.Serializable

open class Effect(
        val name: String,
        open val eID: String,
        open var duration: Int,
        val buff: Boolean = false,
        val loseTurn: Boolean = false,
        val atk: Int = 0,
        val dfp: Int = 0,
        val dmg: Int = 0,
        val sav: Int = 0,
        val curProt: Int = 0,
        val atkDly: Int = 0,
        val movDly: Int = 0
) : Serializable {
    val entity
        get() = GameStore.getByID(eID)!!

    //most effects reset duration on merge
    open fun onMerge(eff: Effect) {
        duration = eff.duration
        log(clock, "Effect", "$name effect renewed")
    }

    open fun onApply() {
        log(clock, "Effect", "$name effect applied")
    }

    open fun onExpire() {
        log(clock, "Effect", "$name effect expired")
    }

    open fun tick() {}

    override fun toString(): String {
        return "[${if (buff) "Green" else "Crimson"}]$name ($duration)"
    }
}

class StunEffect(
        override val eID: String,
        override var duration: Int
) : Effect("Stunned", eID, duration = duration, loseTurn = true)

class RegenEffect(
        override val eID: String,
        override var duration: Int,
        val effPowa: Double = 0.0
) : Effect("Regen", eID, duration = duration, buff = true) {
    var accVit = 0.0
    override fun tick() {
        accVit += effPowa
        if (accVit > 1.0) {
            accVit -= 1.0
            entity.heal(1)
        }
    }
}

class HasteEffect(
        override val eID: String,
        override var duration: Int
) : Effect("Haste", eID, duration = duration, atkDly = -5, movDly = -5, buff = true)
