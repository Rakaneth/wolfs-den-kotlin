package wolfsden.system

import squidpony.squidmath.Coord
import wolfsden.entity.Entity

object Location {
    fun sameMap(e1: Entity, e2: Entity): Boolean = e1.pos?.mapID == e2.pos?.mapID
    fun isOn(thing: Entity, doorMat: Entity): Boolean = sameMap(thing, doorMat) && thing.pos!!.coord.equals(doorMat.pos!!.coord)
    fun visible(e: Entity, c: Coord): Boolean = e.vision?.visible!![c.x][c.y] > 0.0
}