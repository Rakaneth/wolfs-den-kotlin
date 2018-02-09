package wolfsden.system

import squidpony.squidmath.Coord
import wolfsden.entity.Entity
import wolfsden.entity.Position


object Location {
    fun sameMap(e1: Entity, e2: Entity): Boolean = e1.pos?.mapID == e2.pos?.mapID
    fun isOn(thing: Entity, doorMat: Entity): Boolean = sameMap(thing,
                                                                doorMat) && thing.pos!!.coord == doorMat.pos!!.coord

    fun thingsAt(c: Coord,
                 mapID: String) = GameStore.allEntities.filter { it.pos?.mapID == mapID && it.pos?.coord!! == c }

    fun thingsAt(pos: Position) = thingsAt(pos.coord, pos.mapID)
}