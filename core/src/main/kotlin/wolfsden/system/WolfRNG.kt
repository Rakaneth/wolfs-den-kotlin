package wolfsden.system

import squidpony.squidmath.Dice
import squidpony.squidmath.StatefulRNG

object WolfRNG {
    var wolfRNG = StatefulRNG(0xDEADBEEF)
    var dice = Dice(wolfRNG)
}