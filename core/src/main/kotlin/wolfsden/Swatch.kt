package wolfsden

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import squidpony.squidmath.StatefulRNG

val RNG = StatefulRNG(0xDEADBEEF)

object Chars {
    val WALL = '\u2588'
    val FLOOR = '\u00A0'
    val UP = '<'
    val DOWN = '>'
    val OUT = '\u2581'
    val CLOSED = '+'
    val OPEN = '\\'
    val BRIDGE = ':'
}

enum class CommonColors(val color: String) {
    INFO("Light Blue"),
    WARNING("Amber"),
    VIT("Crimson"),
    METAL("Silver"),
    XP("Green");

    val colorObj: Color
        get() = Colors.get(color)
}