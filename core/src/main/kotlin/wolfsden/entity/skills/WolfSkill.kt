package wolfsden.entity.skills

import squidpony.squidai.AOE
import squidpony.squidai.AimLimit
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
    val cost: Int = 0,
    val targeting: Boolean = true,
    val skillIndex: Int = 0,
    val isAttack: Boolean = true
) : Technique(name, aoe) {
    init {
        updateMap()
        aoe.limitType = AimLimit.EIGHT_WAY
    }

    var curCD = cd
    val markupString
        get() = "[${if (isAvailable) CommonColors.INFO else "Gray"}]$name ${if (!isAvailable) " ($curCD)" else ""}[]"

    open val isAvailable
        get() = curCD <= 0 && user.vit!!.curEnd >= cost

    val user
        get() = GameStore.getByID(userID)!!

    fun tick() {
        if (curCD > 0) curCD--
    }

    fun setCD(value: Int = cd) {
        curCD = value
    }

    fun resetCD() {
        curCD = 0
    }

    fun updateMap() {
        setMap(user.getMap().baseMap)
    }

    protected fun targetEntity(c: Coord): Entity? {
        val affected = apply(user.pos!!.coord, c)
        val spot = affected.filter { it.value > 0.0 }.keys.first()
        return Location.thingsAt(spot, user.pos!!.mapID).firstOrNull { it.isCreature }
    }

    protected fun allTargets(c: Coord): List<Entity> {
        val affected = apply(user.pos!!.coord, c)
        val spots = affected.filter { it.value > 0.0 }.keys
        return spots.mapNotNull { Location.thingsAt(it, user.pos!!.mapID).firstOrNull { it.isCreature } }.toList()
    }

    open fun canTarget(target: Coord): Boolean {
        return super.canTarget(user.pos!!.coord, target)
    }

    abstract fun use(target: Coord): Int

    override fun toString(): String = name
}