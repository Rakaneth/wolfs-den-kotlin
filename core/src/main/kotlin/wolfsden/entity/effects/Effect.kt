package wolfsden.entity.effects

import wolfsden.log
import wolfsden.screen.PlayScreen
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
    val movDly: Int = 0,
    val permanent: Boolean = false,
    val tags: List<String> = listOf(),
    val message: String = ""
) : Serializable {
    val entity
        get() = GameStore.getByID(eID)!!

    //most effects reset duration on merge
    open fun onMerge(eff: Effect) {
        duration = eff.duration
        log(clock, "Effect", "$name effect renewed")
    }

    open fun onApply() {
        if (message.isNotEmpty()) PlayScreen.addMessage(message)
        log(clock, "Effect", "$name effect applied")
    }

    open fun onExpire() {
        log(clock, "Effect", "$name effect expired")
    }

    open fun tick() {
        if (!permanent) duration--
    }

    override fun toString(): String {
        return "[${if (buff) "Green" else "Crimson"}]$name${if (permanent) "" else " ($duration)"}"
    }
}



