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

    private fun tryMoveBy(entity: Entity, dx: Int, dy: Int): Boolean {
        val m = entity.getMap()
        val newC = entity.pos!!.coord.translate(dx, dy)
        when {
            m!!.walkable(newC) -> {
                entity.pos!!.x = newC.x
                entity.pos!!.y = newC.y
                entity.updateFOV()
                return true
            }
            else -> return false //TODO: collision detection
        }
    }

    private fun tryMoveBy(entity: Entity, d: Direction): Boolean {
        return tryMoveBy(entity, d.deltaX, d.deltaY)
    }

    fun process(entity: Entity, cmd: String, target: Any? = null) {
        var result = 0
        when (cmd) {
            "move" -> {
                if (tryMoveBy(entity, target as Direction)) {
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
                        tgt.heal?.use(entity)
                        tgt.repair?.use(entity)
                        tgt.rest?.use(entity)
                        entity.removeFromBags(tgt)
                        if (entity.playerVisible()) {
                            PlayScreen.addMessage("${entity.markupString} uses ${tgt.markupString}.")
                        }
                        GameStore.removeEntity(tgt)
                        result = 10
                    }
                    tgt.eq != null -> {
                        entity.putOn(tgt)
                        entity.inventory.remove(tgt)
                        if (entity.playerVisible()) {
                            PlayScreen.addMessage("${entity.markupString} equips ${tgt.markupString}.")
                            result = 10
                        }
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