package wolfsden.system

import squidpony.squidgrid.Direction
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.entity.Entity
import wolfsden.joinWithAnd
import wolfsden.log
import wolfsden.map.WolfMap
import wolfsden.screen.PlayScreen
import wolfsden.system.Location.thingsAt

object CommandProcessor {
    enum class CollideResults { ENEMY, ALLY, DOOR, NONE }

    data class CombatResults(
            val attacker: Entity,
            val defender: Entity,
            val hit: Boolean = false,
            val hitBy: Int = 0,
            val dmg: Int = 0,
            val weakness: List<String> = listOf(),
            val resistance: List<String> = listOf()
    )

    private fun describeCombat(result: CombatResults) {
        with(result) {
            val warning = CommonColors.WARNING
            val info = CommonColors.INFO
            val vit = CommonColors.VIT
            val verb: String = if (hit) {
                val pctDmg = maxOf(dmg * 100 / defender.maxVit, 0)
                when (pctDmg) {
                    0 -> "[$warning]fails to harm[]"
                    in (1 until 10) -> "[$warning]nicks[]"
                    in (10 until 30) -> "[$warning]strikes[]"
                    in (30 until 50) -> "[$warning]soundly strikes[]"
                    else -> "[$vit]wrecks[]"
                }
            } else {
                when (result.hitBy) {
                    in (1 until defender.stats!!.spd) -> "[$warning]barely misses[]"
                    in (defender.stats!!.spd until defender.dfp) -> "[$warning]clashes with[]"
                    else -> "[$warning]misses[]"
                }
            }
            val wk = if (weakness.isNotEmpty()) {
                ", exposing ${defender.markupString}'s ${weakness.joinWithAnd()} weakness"
            } else {
                ""
            }
            val res = if (resistance.isNotEmpty()) {
                ", meeting ${defender.markupString}'s ${resistance.joinWithAnd()} resistance"
            } else {
                ""
            }
            val dm = if (hit && dmg > 0) ", dealing [$vit]$dmg damage[]" else ""
            val finalString = "${attacker.markupString} $verb ${defender.markupString}$wk$res$dm!"
            PlayScreen.addMessage(finalString)
        }
    }

    private fun Entity.tryMoveBy(dx: Int, dy: Int): Pair<CollideResults, Any?> {
        val m = this.getMap()
        val newC = this.pos!!.coord.translate(dx, dy)
        val collider = thingsAt(newC, m!!.id).firstOrNull { it.blocking && it.hasTag("creature") }
        return when {
            m.walkable(newC) -> {
                this.pos!!.x = newC.x
                this.pos!!.y = newC.y
                this.updateFOV()
                Pair(CollideResults.NONE, null)
            }
            collider != null -> {
                when {
                    this.isEnemy(collider) -> Pair(CollideResults.ENEMY, collider)
                    else -> Pair(CollideResults.ALLY, collider)
                }
            }
            m.isDoor(newC) -> Pair(CollideResults.DOOR, newC)
            else -> return Pair(CollideResults.NONE, null)
        }
    }

    private fun Entity.tryMoveBy(d: Direction): Pair<CollideResults, Any?> {
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
        for (thing in things.filter { it != this }) {
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

    private fun Entity.swap(other: Entity) {
        val temp = this.pos!!.coord
        this.pos!!.x = other.pos!!.x
        this.pos!!.y = other.pos!!.y
        other.pos!!.x = temp.x
        other.pos!!.y = temp.y
    }

    private fun Entity.autoAttack(other: Entity) {
        //TODO: remove temp implementation
        val hitPair = WolfRNG.roll(this.atk, other.dfp)
        val hit = hitPair.first > 0
        val hitBy = hitPair.second
        val dmgPair = if (hit) WolfRNG.roll((hitPair.first - 1) * 2 + this.dmg) else 0 to 0
        var dmg = dmgPair.first
        val wk: MutableList<String> = mutableListOf()
        val res: MutableList<String> = mutableListOf()
        this.atkTags.forEach {
            when {
                other.hasWeakness(it) -> {
                    wk.add(it)
                    dmg = (dmg * 1.5).toInt()
                }
                other.hasResistance(it) -> {
                    res.add(it)
                    dmg = (dmg * 0.5).toInt()
                }
                else -> {
                }
            }
        }
        //other.takeDmg(dmg)
        describeCombat(CombatResults(this, other, hit, hitBy, dmg, wk, res))
    }

    fun process(entity: Entity, cmd: String, target: Any? = null) {
        var result = 0
        when (cmd) {
            "move" -> {
                val (moveResult, tgt) = entity.tryMoveBy(target as Direction)
                when (moveResult) {
                    CollideResults.ALLY -> {
                        entity.swap(tgt as Entity)
                        result = entity.movDly
                    }
                    CollideResults.ENEMY -> {
                        entity.autoAttack(tgt as Entity)
                        result = entity.atkDly
                    }
                    CollideResults.DOOR -> {
                        entity.getMap()!!.openDoor(tgt as Coord)
                        result = entity.movDly
                    }
                    else -> {
                        result = entity.movDly
                    }
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
            "attack" -> {
                val tgt = target as Entity
                entity.autoAttack(tgt)
                result = entity.atkDly
            }
            "stairs" -> {
                val tgt = target as WolfMap.Connection
                entity.changeLevel(tgt)
                result = entity.movDly
            }
            else -> {
            } //TODO: other cmds
        }

        GameStore.update()
        if (result > 0 && entity.isPlayer) Scheduler.resume()
        entity.ai!!.delay = result
    }
}