package wolfsden.system

import squidpony.squidmath.Coord
import wolfsden.entity.Entity
import wolfsden.screen.PlayScreen

fun Entity.pickUp() {
    val things = GameStore.entityList.values.filter { Location.isOn(this, it) }
    for (thing in things.filter { it != this}) {
        when {
            !this.bagsFull -> {
                if (this.playerVisible()) PlayScreen.addMessage("${this.markupString} picks up a ${thing.markupString}.")
                this.putInBags(thing)
            }
            this.isPlayer -> PlayScreen.addMessage("${this.markupString}'s bags are too full for ${thing.markupString}.")
            else -> {
            }
        }
    }
    GameStore.update(false, true)
}

object Location {
    fun sameMap(e1: Entity, e2: Entity): Boolean = e1.pos?.mapID == e2.pos?.mapID
    fun isOn(thing: Entity, doorMat: Entity): Boolean = sameMap(thing, doorMat) && thing.pos!!.coord == doorMat.pos!!.coord
    fun thingsAt(c: Coord, mapID: String) = GameStore.entityList.values.filter { it.pos?.mapID == mapID && it.pos?.coord!! == c }

}