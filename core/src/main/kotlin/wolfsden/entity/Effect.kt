package wolfsden.entity

import java.io.Serializable

open class Effect(
        val name: String,
        open var duration: Int,
        val loseTurn: Boolean = false,
        val atk: Int = 0,
        val dfp: Int = 0,
        val dmg: Int = 0,
        val sav: Int = 0,
        val curProt: Int = 0,
        val atkDly: Int = 0,
        val movDly: Int = 0,
        val lpt: Double = 0.0,
        val ept: Double = 0.0
) : Serializable {
    private var accVit = 0.0
    private var accEnd = 0.0

    //most effects reset duration on merge
    open fun onMerge(eff: Effect) {
        duration = eff.duration
    }
}

class StunEffect(
        override var duration: Int
) : Effect("stunned", duration, loseTurn = true)
