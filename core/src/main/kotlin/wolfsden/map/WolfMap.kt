package wolfsden.map

import squidpony.squidgrid.gui.gdx.MapUtility
import squidpony.squidgrid.mapping.DungeonUtility
import wolfsden.Chars
import wolfsden.RNG

class WolfMap (var baseMap: Array<CharArray>, var light: Boolean = true) {
    var displayMap: Array<CharArray> = Array(baseMap.size, { CharArray(baseMap[0].size, {'#'}) })

    init {
        for ((x, row) in baseMap.withIndex()) {
            for ((y, c) in row.withIndex()) {
                displayMap[x][y] = when (c) {
                    '#' -> Chars.WALL
                    '>' -> Chars.DOWN
                    '<' -> Chars.UP
                    '+' -> Chars.CLOSED
                    '\\' -> Chars.OPEN
                    ':' -> Chars.BRIDGE
                    else -> Chars.FLOOR
                }
            }
        }
    }

    val utility = DungeonUtility(RNG)
    var bgFloats = MapUtility.generateDefaultBGColorsFloat(displayMap)
    var fgFloats = MapUtility.generateDefaultColorsFloat(displayMap)
    var resistances = DungeonUtility.generateResistances(displayMap)
}