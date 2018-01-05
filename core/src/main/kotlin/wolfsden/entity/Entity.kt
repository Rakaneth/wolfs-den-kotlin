package wolfsden.entity

import com.badlogic.gdx.graphics.Color
import squidpony.panel.IColoredString
import squidpony.squidmath.Coord
import wolfsden.CommonColors
import wolfsden.nz
import wolfsden.toICString
import java.io.Serializable

val BESTIARY: MutableMap<String, Entity> = mutableMapOf()

class Entity(
        val eID: String,
        var id: Identity? = null,
        var draw: Drawing? = null,
        var pos: Position? = null,
        var vit: Vitals? = null,
        var eq: EquipStats? = null,
        var heal: HealingItem? = null,
        var repair: RepairItem? = null,
        var rest: RestoreItem? = null,
        var stats: Stats? = null,
        var mh: EquipStats? = null,
        var oh: EquipStats? = null,
        var armor: EquipStats? = null,
        var trinket: EquipStats? = null,
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
    val inventory: MutableList<Entity> = mutableListOf()
    val bagsFull
        get() = inventory.size >= 10
    private fun markupEQ(label: String, eq: EquipStats?): IColoredString<Color> {
        return "[${CommonColors.INFO}]%8s[]: ${eq?.name ?: "Nothing"}".format(label).toICString()
    }
    val armorMarkup
        get() =  markupEQ("Mainhand", armor)
    val trinketMarkup
        get() = markupEQ("Trinket", trinket)
    val mhMarkup
        get() = markupEQ("Mainhand", mh)
    val ohMarkup
        get() = markupEQ("Offhand", oh)



    fun addID(name: String, desc: String) {
        id = Identity(eID, name, desc)
    }

    fun addDraw(glyph: Char, color: String, layer: Int) {
        draw = Drawing(eID, glyph, color, layer)
    }

    fun addPos(c: Coord, mapID: String) {
        pos = Position(eID, mapID, c.x, c.y)
    }

    fun addPos(x: Int, y: Int, mapID: String) {
        pos = Position(eID, mapID, x, y)
    }

    fun addEQ(slot: Slot, atk: Int = 0, dfp: Int = 0, dmg: Int = 0, sav: Int = 0, dly: Int = 0, prot: Int = 0) {
        eq = EquipStats(eID, slot, atk, dfp, dmg, sav, dly, prot, prot)
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

    fun addRestore(flatAmt: Int, pctAmt: Float) {
        rest = RestoreItem(eID, pctAmt, flatAmt)
    }

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun putOn(item: Entity) {
        when (item.eq!!.slot) {
            Slot.TWOH -> {
                takeOff(Slot.MH)
                takeOff(Slot.OH)
                equip(item, Slot.MH)
            }
            Slot.AMBI -> {
                when {
                    mh == null -> equip(item, Slot.MH)
                    mh != null && oh == null -> equip(item, Slot.OH)
                    else -> {
                        takeOff(Slot.MH)
                        equip(item, Slot.MH)
                    }
                }
            }
            else -> {
                takeOff(item.eq!!.slot)
                equip(item, item.eq!!.slot)
            }
        }
    }

    fun takeOff(slot: Slot) {
        var toProcess: EquipStats?
        when (slot) {
            Slot.MH -> {
                toProcess = mh; mh = null
            }
            Slot.OH -> {
                toProcess = oh; oh = null
            }
            Slot.ARMOR -> {
                toProcess = armor; armor = null
            }
            else -> {
                toProcess = trinket; trinket = null
            }
        }
        if (toProcess != null) {
            if (bagsFull) {
                toProcess?.getEntity!!.addPos(pos!!.coord, pos!!.mapID)
            } else {
                putInBags(toProcess?.getEntity!!)
            }
        }
    }

    fun equip(item: Entity, slot: Slot) {
        when (slot) {
            Slot.MH -> mh = item.eq
            Slot.OH -> oh = item.eq
            Slot.ARMOR -> armor = item.eq
            Slot.TRINKET -> trinket = item.eq
        }
        item.pos = null
    }

    fun putInBags(item: Entity) {
        item.pos = null
        inventory.add(item)
    }

    fun removeFromBags(item: Entity) {
        inventory.remove(item)
        item.addPos(pos!!.coord, pos!!.mapID)
    }

    fun visible(c: Coord): Boolean = if (vision == null) false else vision!!.visible!![c.x][c.y] > 0.0

    fun visible(other: Entity): Boolean = if (other.pos == null) false else visible(other.pos!!.coord)

    fun repair(flatAmt: Int = 0, pctAmt: Float = 0f) {
        val cArm = (pctAmt * armor?.prot.nz()).toInt()
        val cOH = (pctAmt * oh?.prot.nz()).toInt()
        val tArm = flatAmt + cArm
        val tOH = flatAmt + cOH

        armor?.curProt = minOf(armor?.curProt.nz() + tArm, armor?.prot.nz())
        oh?.curProt = minOf(oh?.curProt.nz() + tOH, oh?.prot.nz())
    }

    fun heal(flatAmt: Int = 0, pctAmt: Float = 0f) {
        val tVit = ((pctAmt * maxVit) + flatAmt).toInt()
        if (vit == null) {
            return
        } else {
            vit!!.curVit = minOf(vit!!.curVit + tVit, maxVit)
        }
    }

    fun restore(flatAmt: Int = 0, pctAmt: Float = 0f) {
        val tEnd = ((pctAmt * maxVit) + flatAmt).toInt()
        if (vit == null) {
            return
        } else {
            vit!!.curEnd = minOf(vit!!.curEnd + tEnd, maxEnd)
        }
    }

    fun hasTag(tag: String): Boolean = tags.contains(tag)

    override fun toString(): String = "${id?.name}-${eID.substringBefore("-")}"

}