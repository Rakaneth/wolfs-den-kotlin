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

fun Entity.attack(other: Entity, atkStat: Int = this.atk, defStat: Int = other.dfp, dmgMod: Int = 0): CombatResults {
    val (sux, hitBy) = WolfRNG.roll(atkStat, defStat)
    val hit = sux > 0
    var (dmg, _) = if (hit) WolfRNG.roll((sux - 1) * 2 + this.dmg + dmgMod) else 0 to 0
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
                dmg = (dmg * 0.75).toInt()
            }
            else -> {
            }
        }
    }
    return CombatResults(this, other, hit, hitBy, dmg, wk, res)
}

data class CombatResults(
        val attacker: Entity,
        val defender: Entity,
        val hit: Boolean = false,
        val hitBy: Int = 0,
        val dmg: Int = 0,
        val weakness: List<String> = listOf(),
        val resistance: List<String> = listOf()
)

object CommandProcessor {
    enum class CollideResults { ENEMY, ALLY, DOOR, NONE }

    private fun autoAttack(atkr: Entity, defr: Entity) {
        val results = atkr.attack(defr)
        if (results.hit) defr.takeDmg(results.dmg)
        describeCombat(results)
    }

    private fun describeCombat(result: CombatResults) {
        with(result) {
            val warning = CommonColors.WARNING
            val vit = CommonColors.VIT
            val info = CommonColors.INFO
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
                when (hitBy) {
                    in (1 until defender.stats!!.spd) -> "[$warning]barely misses[]"
                    in (defender.stats!!.spd until defender.dfp) -> "[$warning]clashes with[]"
                    else -> "[$warning]misses[]"
                }
            }
            val wk = if (weakness.isNotEmpty()) {
                ", exposing ${defender.markupString}'s [$info]${weakness.joinWithAnd()}[] weakness"
            } else {
                ""
            }
            val res = if (resistance.isNotEmpty()) {
                ", meeting ${defender.markupString}'s [$info]${resistance.joinWithAnd()}[] resistance"
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

    fun process(entity: Entity, cmd: String, target: Any? = null) {
        val result = when (cmd) {
            "move" -> {
                val (moveResult, tgt) = entity.tryMoveBy(target as Direction)
                when (moveResult) {
                    CollideResults.ALLY -> {
                        entity.swap(tgt as Entity)
                        entity.movDly
                    }
                    CollideResults.ENEMY -> {
                        autoAttack(entity, tgt as Entity)
                        entity.atkDly
                    }
                    CollideResults.DOOR -> {
                        entity.getMap()!!.openDoor(tgt as Coord)
                        entity.movDly
                    }
                    else -> {
                        entity.movDly
                    }
                }
            }
            "pickup" -> {
                entity.pickUp()
                10
            }
            "use" -> {
                val tgt = target as Entity
                when {
                    tgt.heal != null || tgt.repair != null || tgt.rest != null -> {
                        entity.tryUse(tgt)
                        10
                    }
                    tgt.eq != null -> {
                        entity.tryEQ(tgt)
                        10
                    }
                    else -> {
                        log(Scheduler.clock, "CommandProcessor", "${entity.eID} attempts to use unusable item ${tgt.eID}")
                        10
                    }
                }
            }
            "attack" -> {
                val tgt = target as Entity
                autoAttack(entity, tgt)
                entity.atkDly
            }
            "stairs" -> {
                val tgt = target as WolfMap.Connection
                entity.changeLevel(tgt)
                entity.movDly
            }
            "wait" -> {
                10
            }
            else -> {
                0
            } //TODO: other cmds
        }

        GameStore.update()
        if (result > 0 && entity.isPlayer) Scheduler.resume()
        entity.ai!!.delay = result
    }
}