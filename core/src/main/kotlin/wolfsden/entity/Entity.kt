package wolfsden.entity

import squidpony.squidmath.Coord
import java.io.Serializable
import java.util.*

val BESTIARY: MutableMap<String, Entity> = mutableMapOf()

class Entity(
        var id: Identity? = null,
        var draw: Drawing? = null,
        var pos: Position? = null,
        var vit: Vitals? = null,
        var eq: Equipment? = null,
        var heal: HealingItem? = null,
        var repair: RepairItem? = null,
        var stats: Stats? = null,
        var mh: Equipment? = null,
        var oh: Equipment? = null,
        var armor: Equipment? = null,
        var trinket: Equipment? = null,
        var xp: XPGainer? = null,
        var isPlayer: Boolean = false
) : Serializable {
    val eID = UUID.randomUUID().toString()


    val atk: Int
        get() = (stats?.skl ?: 0) + (armor?.atk ?: 0) + (mh?.atk ?: 0) + (oh?.atk ?: 0) + (trinket?.atk ?: 0)
    val dfp: Int
        get() = (stats?.spd ?: 0) + (armor?.dfp ?: 0) + (mh?.dfp ?: 0) + (oh?.dfp ?: 0) + (trinket?.dfp ?: 0)
    val dmg: Int
        get() = (stats?.str ?: 0) + (armor?.dmg ?: 0) + (mh?.dmg ?: 0) + (oh?.dmg ?: 0) + (trinket?.dmg ?: 0)
    val sav: Int
        get() = (stats?.stam ?: 0) + (armor?.sav ?: 0) + (mh?.sav ?: 0) + (oh?.sav ?: 0) + (trinket?.sav ?: 0)
    val atkDly: Int
        get() = maxOf(5 + maxOf(mh?.dly ?: 0, oh?.dly ?: 0) - (stats?.skl ?: 0), 1)
    val movDly: Int
        get() = maxOf(5 + (armor?.dly ?: 0) - (stats?.spd ?: 0), 1)
    val markupString: String?
        get() = if (draw == null || id == null) null else "[${draw?.color}]${id?.name}[]"


    fun addID(name: String, desc: String) {
        id = Identity(eID, name, desc)
    }

    fun addDraw(glyph: Char, color: String) {
        draw = Drawing(eID, glyph, color)
    }

    fun addPos(c: Coord, mapID: String) {
        pos = Position(eID, mapID, c.x, c.y)
    }

    fun addPos(x: Int, y: Int, mapID: String) {
        pos = Position(eID, mapID, x, y)
    }

    fun addEQ(slot: Slot, atk: Int = 0, dfp: Int = 0, dmg: Int = 0, sav: Int = 0, dly: Int = 0) {
        eq = Equipment(eID, slot, atk, dfp, dmg, sav, dly)
    }

    fun addVitals(alive: Boolean, curVit: Int, maxVit: Int, curEnd: Int, maxEnd: Int) {
        vit = Vitals(eID, alive, curVit, maxVit, curEnd, maxEnd)
    }

    fun addStats(str: Int = 0, stam: Int = 0, spd: Int = 0, skl: Int = 0) {
        stats = Stats(eID, str, stam, spd, skl)
    }

    fun addHeal(flatAmt: Int = 0, pctAmt: Float = 0f) {
        heal = HealingItem(eID, pctAmt, flatAmt)
    }

    fun addRepair(flatAmt: Int = 0, pctAmt: Float = 0f) {
        repair = RepairItem(eID, pctAmt, flatAmt)
    }

    fun addXP(curXP: Float = 0f, totXP: Float = 0f) {
        xp = XPGainer(eID, curXP, totXP)
    }

}