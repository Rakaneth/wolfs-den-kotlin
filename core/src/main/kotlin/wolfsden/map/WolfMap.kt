package wolfsden.map

import squidpony.ArrayTools
import squidpony.squidgrid.gui.gdx.MapUtility
import squidpony.squidgrid.mapping.DungeonUtility
import squidpony.squidmath.Coord
import squidpony.squidmath.CoordPacker
import wolfsden.Chars
import wolfsden.system.WolfRNG
import java.io.Serializable

class WolfMap(val id: String, var baseMap: Array<CharArray>, var light: Boolean = true) : Serializable {
    var displayMap: Array<CharArray> = ArrayTools.fill('#', baseMap.size, baseMap[0].size)

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

    @Transient
    val utility = DungeonUtility(WolfRNG.wolfRNG)
    var bgFloats = MapUtility.generateDefaultBGColorsFloat(displayMap)
    var fgFloats = MapUtility.generateDefaultColorsFloat(displayMap)
    var resistances = DungeonUtility.generateResistances(displayMap)

    fun randomFloor(): Coord = utility.randomFloor(displayMap)
 }