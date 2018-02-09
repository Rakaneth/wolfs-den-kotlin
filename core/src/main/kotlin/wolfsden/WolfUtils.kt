package wolfsden

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.Color
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.GDXMarkup
import squidpony.squidgrid.gui.gdx.TextCellFactory
import squidpony.squidmath.Coord
import wolfsden.entity.Entity
import wolfsden.screen.WolfScreen

private var debugState = true

fun <T : Comparable<T>> T.between(low: T, high: T): Boolean {
    return when {
        this < low -> false
        this > high -> false
        else -> true
    }
}

fun Int?.nz(value: Int = 0) = this ?: value

fun log(time: Int, tag: String, msg: String) {
    if (debugState) GdxAI.getLogger().info(tag, "[$time]$msg")
}

fun String.toICString(): IColoredString<Color> {
    return GDXMarkup.instance.colorString(this)
}

fun List<String>.joinWithAnd(): String {
    var accum = ""
    when (this.size) {
        1 -> accum = this[0]
        2 -> accum = "${this[0]} and ${this[1]}"
        else ->
            for (idx in this.indices) {
                accum += if (idx in 0..this.size - 2) "${this[idx]}, " else "and ${this[idx]}"
            }
    }
    return accum
}

fun TextCellFactory.setUp(width: Float = WolfScreen.cellWidth, height: Float = WolfScreen.cellHeight,
                          tw: Float = 1f, th: Float = 1f): TextCellFactory {
    return this.width(width)
            .height(height)
            .tweakWidth(tw * width)
            .tweakHeight(th * height)
            .initBySize()
}

fun Collection<Entity>.getCoords() = this.mapNotNull { it.pos?.coord }
fun Collection<Coord>.nearest(toCoord: Coord): Coord = this.minBy { it.distance(toCoord) }!!
fun Collection<Coord>.farthest(fromCoord: Coord): Coord = this.maxBy { it.distance(fromCoord) }!!

val fib = intArrayOf(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946,
                     17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309, 3524578,
                     5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 165580141, 267914296,
                     433494437, 701408733, 1134903170, 1836311903)


