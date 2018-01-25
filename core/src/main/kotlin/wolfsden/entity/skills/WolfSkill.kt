package wolfsden.entity.skills

import squidpony.squidai.AOE
import squidpony.squidai.Technique
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.system.GameStore
import wolfsden.system.getMap

abstract class WolfSkill(
        open val userID: String,
        name: String,
        val desc: String,
        aoe: AOE,
        val cd: Int = 0
) : Technique(name, aoe) {
    init {
        updateMap()
    }

    var curCD = cd
    val markupString
        get() = "[${CommonColors.INFO}]$name[]"

    open val isAvailable
        get() = curCD <= 0

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

    abstract fun use(target: Coord): Int
}