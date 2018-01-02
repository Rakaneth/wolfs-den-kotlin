package wolfsden.system

import squidpony.squidmath.Coord
import wolfsden.entity.Entity
import wolfsden.screen.PlayScreen

object Location {
    fun sameMap(e1: Entity, e2: Entity): Boolean = e1.pos?.mapID == e2.pos?.mapID
    fun isOn(thing: Entity, doorMat: Entity): Boolean = sameMap(thing, doorMat) && thing.pos!!.coord == doorMat.pos!!.coord
    fun thingsAt(c: Coord, mapID: String) = GameStore.entityList.values.filter { it.pos?.mapID == mapID && it.pos?.coord!! == c }
    fun pickUp(entity: Entity) {
        val things = GameStore.entityList.values.filter {isOn(entity, it)}
        for (thing in things) {
            when {
                !entity.bagsFull -> {
                    entity.putInBags(thing)
                    if (entity.playerVisible()) PlayScreen.addMessage("${entity.markupString} picks up a ${thing.markupString}.")
                }
                entity.isPlayer -> PlayScreen.addMessage("${entity.markupString}'s bags are too full for ${thing.markupString}.")
                else -> {}
            }
        }
    }
}