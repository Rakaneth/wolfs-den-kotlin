package wolfsden

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.XmlReader
import squidpony.panel.IColoredString
import squidpony.squidgrid.gui.gdx.GDXMarkup


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
    GdxAI.getLogger().info(tag, "[$time]$msg")
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
                accum += if (idx in 0..this.size-2) "${this[idx]}, "  else "and ${this[idx]}"
            }
    }
    return accum
}
