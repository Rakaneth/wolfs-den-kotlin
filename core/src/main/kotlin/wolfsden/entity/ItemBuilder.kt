package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidmath.Coord
import squidpony.squidmath.ProbabilityTable
import wolfsden.log
import wolfsden.nz
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG
import java.util.*

object ItemBuilder {
    private const val itemFile = "data/equipment.xml"
    private val itemBP = XmlReader().parse(Gdx.files.internal(itemFile))

    fun build(buildID: String, mapID: String, start: Coord? = null): Entity {
        val info = itemBP.getChildrenByName("EntityType").first { it["id"] == buildID }
        val mold = Entity(UUID.randomUUID().toString())

        val id = info.getChildByName("identity")
        val draw = info.getChildByName("draw")
        val pos = info.getChildByName("pos")
        val toGlyph = if (draw["glyph"].length == 1) {
            draw["glyph"].toCharArray().first()
        } else {
            draw["glyph"].toInt(16).toChar()
        }

        val toStart = if (pos == null) {
            start ?: GameStore.getMapByID(mapID).randomFloor()
        } else {
            start ?: Coord.get(pos["x"].toInt(), pos["y"].toInt())
        }

        mold.addID(id["name"]!!, id["desc"])
        mold.addDraw(toGlyph, draw["color"], 1)
        mold.addPos(toStart, mapID)
        log(0, "ItemBuilder", "Item $buildID created at ${mold.pos!!.coord} on floor $mapID")

        info.nz("eqData") {
            val eq = info.getChildByName("eqData")
            val slot = when (eq!!["slot"]) {
                "2h" -> Slot.TWOH
                "ambi" -> Slot.AMBI
                "mh" -> Slot.MH
                "oh" -> Slot.OH
                "armor" -> Slot.ARMOR
                else -> Slot.TRINKET
            }
            val prot: Int = if (eq.hasAttribute("prot")) eq["prot"].toInt() else 0
            mold.addEQ(slot, eq["atk"].toInt(), eq["dfp"].toInt(), eq["dmg"].toInt(), eq["sav"].toInt(), eq["dly"].toInt(), prot)
        }
        info.nz("recoverData") {
            val rc = info.getChildByName("recoverData")
            val type = rc["itemType"]
            val flatAmt = if (rc.hasAttribute("flatAmt")) rc["flatAmt"].toInt() else 0
            val pctAmt = if (rc.hasAttribute("pctAmt")) rc.getAttribute("pctAmt").toFloat() else 0f
            when (type) {
                "healing" -> mold.addHeal(flatAmt, pctAmt)
                "repair" -> mold.addRepair(flatAmt, pctAmt)
                else -> mold.addRestore(flatAmt, pctAmt)
            }
        }

        info.nz("tags") {
            for (tag in info["tags"].split(",")) {
                mold.addTag(tag)
            }
        }
        mold.addTag(info["meta:RefKey"])

        mold.blocking = false
        GameStore.addEntity(mold)
        return mold
    }

    fun seedItems(mapID: String, vararg tags: String) {
        val numItems = WolfRNG.wolfRNG.nextInt(25)
        val table: ProbabilityTable<String> = ProbabilityTable(WolfRNG.wolfRNG)
        val coll = if (tags.isEmpty()) {
            itemBP.getChildrenByName("EntityType").filter { it.hasChild("rarity") }
        } else {
            itemBP.getChildrenByName("EntityType").filter {
                it.hasChild("rarity") && it["tags"].split(",").any { tags.contains(it) }
            }
        }
        for (chosen in coll) {
            table.add(chosen["id"], chosen["rarity"].toInt())
        }

        for (i in 0.until(numItems)) {
            build(table.random(), mapID)
        }
    }
}