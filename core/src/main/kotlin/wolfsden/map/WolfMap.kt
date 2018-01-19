package wolfsden.map

import squidpony.ArrayTools
import squidpony.squidgrid.FOV
import squidpony.squidgrid.gui.gdx.MapUtility
import squidpony.squidgrid.gui.gdx.SColor
import squidpony.squidgrid.mapping.DungeonUtility
import squidpony.squidmath.Coord
import squidpony.squidmath.CoordPacker
import wolfsden.Chars
import wolfsden.between
import wolfsden.system.Location
import wolfsden.system.WolfRNG
import java.io.Serializable

class WolfMap(val id: String, val name: String, var baseMap: Array<CharArray>, var light: Boolean = true) : Serializable {
    var displayMap: Array<CharArray> = ArrayTools.fill('#', baseMap.size, baseMap[0].size)

    init {
        for ((x, row) in baseMap.withIndex()) {
            for ((y, c) in row.withIndex()) {
                displayMap[x][y] = when (c) {
                    '#' -> Chars.WALL
                    '>' -> Chars.DOWN
                    '<' -> Chars.UP
                    '+' -> Chars.CLOSED
                    '/' -> Chars.CLOSED
                    '\\' -> Chars.OPEN
                    ':' -> Chars.BRIDGE
                    else -> Chars.FLOOR
                }
            }
        }
    }

    @Transient
    var utility = DungeonUtility(WolfRNG.wolfRNG)

    var bgFloats = MapUtility.generateDefaultBGColorsFloat(baseMap)
    var fgFloats = MapUtility.generateDefaultColorsFloat(baseMap)
    var resistances = DungeonUtility.generateResistances(baseMap)
    val connections: MutableMap<Coord, Connection> = mutableMapOf()

    val width
        get() = baseMap.size

    val height
        get() = baseMap[0].size

    fun isDoor(c: Coord): Boolean = arrayOf('+', '\\', '/').contains(baseMap[c.x][c.y])

    fun oob(c: Coord): Boolean = !c.x.between(0, width - 1) || !c.y.between(0, height - 1)

    fun walkable(c: Coord): Boolean {
        val canWalk = !oob(c) && arrayOf('\\', '.', '>', '<', ',', ':').contains(baseMap[c.x][c.y])
        val emptyC = Location.thingsAt(c, id).none { it.blocking }
        return canWalk && emptyC
    }

    fun connect(from: Coord, toCoord: Coord, mapID: String) {
        connections[from] = Connection(toCoord, mapID)
    }

    fun connection(from: Coord): Connection {
        require(connections.containsKey(from))
        return connections[from]!!
    }

    fun randomFloor(): Coord = utility.randomFloor(baseMap)

    fun randomFloorWithin(c: Coord, radius: Double = 1.0): Coord {
        val tempVisible = ArrayTools.fill(0.0, baseMap.size, baseMap[0].size)
        FOV.reuseFOV(resistances, tempVisible, c.x, c.y, radius)
        return CoordPacker.singleRandom(CoordPacker.pack(tempVisible), WolfRNG.wolfRNG)
    }

    fun changeMap(c: Coord, baseChar: Char, displayChar: Char) {
        baseMap[c.x][c.y] = baseChar
        displayMap[c.x][c.y] = displayChar
        fgFloats = MapUtility.generateDefaultColorsFloat(baseMap)
        bgFloats = MapUtility.generateDefaultBGColorsFloat(baseMap)
        resistances = DungeonUtility.generateResistances(baseMap)
    }

    fun closeDoor(c: Coord) {
        changeMap(c, '+', Chars.CLOSED)
    }

    fun openDoor(c: Coord) {
        changeMap(c, '\\', Chars.OPEN)
    }

    fun downStair(c: Coord, special: Boolean = false) {
        changeMap(c, '>', Chars.DOWN)
        if (special) fgFloats[c.x][c.y] = (SColor.RED.toFloatBits())
    }

    fun upStair(c: Coord, special: Boolean = false) {
        changeMap(c, '<', Chars.UP)
        if (special) fgFloats[c.x][c.y] = (SColor.RED.toFloatBits())
    }

    data class Connection(val to: Coord, val mapID: String) : Serializable
}