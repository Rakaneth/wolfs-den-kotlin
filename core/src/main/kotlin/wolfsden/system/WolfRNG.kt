package wolfsden.system

import squidpony.squidmath.Dice
import squidpony.squidmath.StatefulRNG
import wolfsden.between

object WolfRNG {
    var wolfRNG = StatefulRNG(0xDEADBEEF)
    private val dice = Dice(wolfRNG)

    //based on Earthdawn's step system, periodic about d10 for simplicity
    private val steps = arrayOf("0", "!4-2", "!4-1", "!4", "!6", "!8", "!10", "!6+!4", "2!6", "!8+!6", "!10+!6", "!10+!8")

    fun diceString(step: Int): String {
        return when {
            step < 0 -> "0"
            step.between(0, 11) -> steps[step]
            else -> "${step/6-1}!10+${steps[step%6+6]}"
        }

    }

    fun roll(step: Int, diff: Int?=null): Int {
        val rl = dice.roll(diceString(step))
        val raw = rl - (diff ?: 0)
        if (diff == null) {
            return rl
        } else {
            return if (raw >= 0) raw / 5 + 1 else 0
        }
    }

    fun testRoll(step: Int, diff: Int?=null) {
        val r = roll(step, diff)
        println("Rolling step $step ${if (diff != null) "against diff $diff" else ""} (${diceString(step)}): $r")
    }

    fun extendedRollTest(step: Int, diff: Int) {
        val pct = Array (100, { roll(step, diff) }).count {it > 0}
        println("Result of 100 step $step (${diceString(step)}) rolls against diff $diff: $pct%")
    }
}