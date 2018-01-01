package wolfsden.system

import squidpony.squidgrid.Direction
import squidpony.squidgrid.FOV
import wolfsden.entity.Entity
import wolfsden.map.WolfMap

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

    fun process(entity: Entity, cmd: String, target: Any) {
        var result = 0
        when (cmd) {
            "move" -> {
                if (tryMoveBy(entity, target as Direction)) {
                    GameStore.update()
                    result = entity.movDly
                }
            }
            else -> {
            } //TODO: other cmds
        }
        if (result > 0 && entity.isPlayer) Scheduler.resume()
        entity.ai!!.delay = result
    }
}