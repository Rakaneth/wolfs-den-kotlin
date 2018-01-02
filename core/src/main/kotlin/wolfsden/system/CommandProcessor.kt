package wolfsden.system

import squidpony.squidgrid.Direction
import squidpony.squidgrid.FOV
import wolfsden.entity.Entity
import wolfsden.log
import wolfsden.map.WolfMap
import wolfsden.screen.PlayScreen

object CommandProcessor {
    fun Entity.getMap(): WolfMap? = GameStore.mapList[this.pos?.mapID]
    fun Entity.updateFOV() {
        FOV.reuseFOV(this.getMap()!!.resistances, this.vision!!.visible, this.pos!!.x, this.pos!!.y, this.vision!!.vision)
    }

    private fun Entity.tryMoveBy(dx: Int, dy: Int): Boolean {
        val m = this.getMap()
        val newC = this.pos!!.coord.translate(dx, dy)
        when {
            m!!.walkable(newC) -> {
                this.pos!!.x = newC.x
                this.pos!!.y = newC.y
                this.updateFOV()
                return true
            }
            else -> return false //TODO: collision detection
        }
    }

    private fun Entity.tryMoveBy(d: Direction): Boolean {
        return this.tryMoveBy(d.deltaX, d.deltaY)
    }

    private fun Entity.tryUse(item: Entity) {
        item.heal?.use(this)
        item.repair?.use(this)
        item.rest?.use(this)
        if (this.playerVisible()) {
            PlayScreen.addMessage("${this.markupString} uses ${item.markupString}.")
        }
        this.removeFromBags(item)
        GameStore.removeEntity(item)
    }

    private fun Entity.tryEQ(item: Entity) {
        this.putOn(item)
        this.inventory.remove(item)
        if (this.playerVisible()) {
            PlayScreen.addMessage("${this.markupString} equips ${item.markupString}.")
        }
    }

    private fun Entity.pickUp() {
        val things = GameStore.entityList.values.filter { Location.isOn(this, it) }
        for (thing in things.filter { it != this}) {
            when {
                !this.bagsFull -> {
                    if (this.playerVisible()) PlayScreen.addMessage("${this.markupString} picks up ${thing.markupString}.")
                    this.putInBags(thing)
                }
                this.isPlayer -> PlayScreen.addMessage("${this.markupString}'s bags are too full for ${thing.markupString}.")
                else -> {
                }
            }
        }
        GameStore.update(false, true)
    }

    fun process(entity: Entity, cmd: String, target: Any? = null) {
        var result = 0
        when (cmd) {
            "move" -> {
                if (entity.tryMoveBy(target as Direction)) {
                    result = entity.movDly
                }
            }
            "pickup" -> {
                entity.pickUp()
                result = 10
            }
            "use" -> {
                val tgt = target as Entity
                when {
                    tgt.heal != null || tgt.repair != null || tgt.rest != null -> {
                        entity.tryUse(tgt)
                        result = 10
                    }
                    tgt.eq != null -> {
                        entity.tryEQ(tgt)
                        result = 10
                    }
                    else -> {
                        log(Scheduler.clock, "CommandProcessor", "${entity.eID} attempts to use unusable item ${tgt.eID}")
                    }
                }
            }
            else -> {
            } //TODO: other cmds
        }

        GameStore.update()
        if (result > 0 && entity.isPlayer) Scheduler.resume()
        entity.ai!!.delay = result
    }
}