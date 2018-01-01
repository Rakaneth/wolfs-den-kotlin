package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidmath.Coord
import wolfsden.nz
import wolfsden.system.GameStore
import java.util.*

object ItemBuilder {
    private val reader= XmlReader()
    private const val itemFile = "data/equipment.xml"
    private val itemBP = reader.parse(Gdx.files.internal(itemFile))

    fun build(buildID: String, mapID: String, start: Coord? = null): Entity {
        val info = itemBP.getChildrenByName("EntityType").filter { it["id"] == buildID }.first()
        val mold = Entity(UUID.randomUUID().toString())

        val id = info.getChildByName("identity")
        val draw = info.getChildByName("draw")
        val pos = info.getChildByName("pos")

        val toStart = if (pos == null) {
            start ?: GameStore.getMapByID(mapID).randomFloor()
        } else {
            start ?: Coord.get(pos["x"].toInt(), pos["y"].toInt())
        }

        mold.addID(id["name"]!!, id["desc"])
        mold.addDraw(draw["glyph"].toCharArray().first(), draw["color"])
        mold.addPos(toStart, mapID)

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
            val flatAmt = rc.getAttribute("flatAmt").toInt()
            val pctAmt = rc.getAttribute("pctAmt").toFloat()
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

        GameStore.addEntity(mold)
        return mold
    }
}