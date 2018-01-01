package wolfsden.entity

import squidpony.squidmath.Coord
import wolfsden.nz
import java.io.Serializable

val BESTIARY: MutableMap<String, Entity> = mutableMapOf()

class Entity(
        val eID: String,
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
        var vision: Vision? = null,
        var ai: AI? = null,
        var isPlayer: Boolean = false,
        var blocking: Boolean = true
) : Serializable {
    val atk: Int
        get() = stats?.skl.nz() + armor?.atk.nz() + mh?.atk.nz() + oh?.atk.nz() + trinket?.atk.nz()
    val dfp: Int
        get() = stats?.spd.nz() + armor?.dfp.nz() + mh?.dfp.nz() + oh?.dfp.nz() + trinket?.dfp.nz()
    val dmg: Int
        get() = stats?.str.nz() + armor?.dmg.nz() + mh?.dmg.nz() + oh?.dmg.nz() + trinket?.dmg.nz()
    val sav: Int
        get() = stats?.stam.nz() + armor?.sav.nz() + mh?.sav.nz() + oh?.sav.nz() + trinket?.sav.nz()
    val atkDly: Int
        get() = maxOf(5 + maxOf(mh?.dly.nz(), oh?.dly.nz()) - stats?.skl.nz(), 1)
    val movDly: Int
        get() = maxOf(5 + armor?.dly.nz() - stats?.spd.nz(), 1)
    val markupString: String?
        get() = if (draw == null || id == null) null else "[${draw?.color}]${id?.name}[]"
    val maxVit: Int
        get() = stats?.stam.nz() * 10
    val maxEnd: Int
        get() = stats?.stam.nz() * 15
    val curArmor: Int
        get() = armor?.curProt.nz() + oh?.curProt.nz()
    val curShield: Int
        get() = mh?.curProt.nz() + trinket?.curProt.nz()
    val maxArmor: Int
        get() = armor?.prot.nz() + oh?.prot.nz()
    val maxShield: Int
        get() = mh?.prot.nz() + trinket?.prot.nz()
    var worthXP: Float = 0f
    private val tags: MutableList<String> = mutableListOf()

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

    fun addEQ(name: String, slot: Slot, atk: Int = 0, dfp: Int = 0, dmg: Int = 0, sav: Int = 0, dly: Int = 0) {
        eq = Equipment(eID, name, slot, atk, dfp, dmg, sav, dly)
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

    fun addVision(radius: Double = 6.0) {
        vision = Vision(eID, radius)
    }

    fun addXP(curXP: Float = 0f, totXP: Float = 0f) {
        xp = XPGainer(eID, curXP, totXP)
    }

    fun addAI(initialDelay: Int, aiTree: String) {
        val toAI = "data/ai/$aiTree.tree"
        ai = AI(eID, initialDelay, toAI)
    }

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun repair(amt: Int) {
        var amtToRepair = armor?.prot.nz() - armor?.curProt.nz()
        var bank = amt
        if (bank > amtToRepair) {
            armor?.curProt = armor?.prot.nz()
            bank -= amtToRepair
        } else {
            armor!!.curProt += amt
            return
        }
        if (bank > 0 && oh != null) {
            oh!!.curProt = minOf(oh!!.prot, oh!!.curProt + bank)
        }
    }

    fun hasTag(tag: String): Boolean = tags.contains(tag)

    override fun toString(): String = "${id?.name}-${eID}"

}