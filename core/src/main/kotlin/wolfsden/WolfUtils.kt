package wolfsden

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.XmlReader
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.GDXMarkup
import squidpony.squidgrid.gui.gdx.TextCellFactory
import wolfsden.screen.WolfScreen

var debugState = false

fun <T : Comparable<T>> T.between(low: T, high: T): Boolean {
    return when {
        this < low -> false
        this > high -> false
        else -> true
    }
}

fun Int?.nz(value: Int = 0) = this ?: value

inline fun XmlReader.Element.nz(attr: String, block: () -> Unit) {
    if (this.hasAttribute(attr) || this.hasChild(attr)) block()
}

fun log(time: Int, tag: String, msg: String) {
    if (debugState) GdxAI.getLogger().info(tag, "[$time]$msg")
}

fun String.toICString(): IColoredString<Color> {
    return GDXMarkup.instance.colorString(this)
}

fun List<String>.joinWithAnd(): String {
    var accum = ""
    when (this.size) {
        1 -> accum = "${this[0]}"
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
