package wolfsden.system

import squidpony.squidmath.Dice
import squidpony.squidmath.StatefulRNG
import wolfsden.between

object WolfRNG {
    var wolfRNG = StatefulRNG(0xDEADBEEF)
    private val dice = Dice(wolfRNG)

    //based on Earthdawn's step system, repeats the second set every 6 steps
    private val steps = arrayOf("0", "!4-2", "!4-1", "!4", "!6", "!8", "!10")
    private val steps2 = arrayOf( "0", "!6+!4", "2!6", "!8+!6", "!10+!6", "!10+!8")

    fun diceString(step: Int): String {
        return when {
            step < 0 -> "0"
            step.between(0, 6) -> steps[step]
            step.between(7, 11) -> steps2[step - 6]
            else -> "${step/6 - 1}!10+${steps2[(step) % 6]}"
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
        println("Rolling step $step (${diceString(step)})${if (diff != null) " against diff $diff" else ""}: $r")
    }

    fun extendedRollTest(step: Int, diff: Int) {
        val pct = Array (1000, { roll(step, diff) }).count {it > 0}
        println("Result of 1000 step $step (${diceString(step)}) rolls against diff $diff: ${pct/10}%")
    }
}