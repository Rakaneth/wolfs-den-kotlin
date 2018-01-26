package wolfsden.entity.skills

import squidpony.squidai.AOE
import squidpony.squidai.Technique
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.entity.Entity
import wolfsden.system.GameStore
import wolfsden.system.Location
import wolfsden.system.getMap

abstract class WolfSkill(
        private val userID: String,
        name: String,
        val desc: String,
        aoe: AOE,
        val cd: Int = 0,
        val cost: Int = 0
) : Technique(name, aoe) {
    init {
        updateMap()
    }

    var curCD = cd
    val markupString
        get() = "[${CommonColors.INFO}]$name[]"

    open val isAvailable
        get() = curCD <= 0 && user.vit!!.curEnd >= cost

    val user
        get() = GameStore.getByID(userID)!!

    fun tick() {
        curCD--
    }

    fun setCD() {
        curCD = cd
    }

    fun resetCD() {
        curCD = 0
    }

    fun updateMap() {
        setMap(user.getMap()!!.baseMap)
    }

    protected fun targetEntity(c: Coord): Entity? {
        val affected = apply(user.pos!!.coord, c)
        val spot = affected.filter { it.value > 0.0 }.keys.first()
        return Location.thingsAt(spot, user.pos!!.mapID).firstOrNull { it.isCreature }
    }

    protected fun allTargets(c: Coord): List<Entity> {
        val affected = apply(user.pos!!.coord, c)
        val spots = affected.filter { it.value > 0.0}.keys
        return spots.mapNotNull { Location.thingsAt(it, user.pos!!.mapID).firstOrNull { it.isCreature}}.toList()
    }

    abstract fun use(target: Coord): Int
}