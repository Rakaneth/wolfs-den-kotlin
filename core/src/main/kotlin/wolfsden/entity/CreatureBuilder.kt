package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.ArrayTools
import squidpony.squidgrid.FOV
import squidpony.squidmath.Coord
import wolfsden.nz
import wolfsden.system.Faction
import wolfsden.system.GameStore
import java.util.*

object CreatureBuilder {
    private val reader = XmlReader()
    private const val creatureFile = "data/monsters.xml"
    private val creatureBP: XmlReader.Element = reader.parse(Gdx.files.internal(creatureFile))

    fun build(buildID: String, isPlayer: Boolean = false, start: Coord? = null, mapID: String? = null, name: String? = null): Entity? {
        val info = creatureBP.getChildrenByName("EntityType").firstOrNull { it["id"] == buildID } ?: return null
        val eID = if (isPlayer) "player" else UUID.randomUUID().toString()
        val foetus = Entity(eID)
        val id = info.getChildByName("identity")
        val draw = info.getChildByName("draw")
        val stats = info.getChildByName("stats")
        val pos = info.getChildByName("pos")
        val toName = name ?: id["name"]

        foetus.addID(toName, id["desc"])
        foetus.addDraw(draw["glyph"].toCharArray().first(), draw["color"], 2)
        info.nz("stats") {
            foetus.addStats(stats["str"].toInt(), stats["stam"].toInt(), stats["spd"].toInt(), stats["skl"].toInt())
        }
        foetus.addVitals(true, foetus.maxVit, foetus.maxVit, foetus.maxEnd, foetus.maxEnd)
        info.nz("worthXP") { foetus.worthXP = info["worthXP"].toFloat() }
        info.nz("gainsXP") { foetus.addXP() }
        foetus.isPlayer = isPlayer

        info.nz("tags") {
            for (tag in info["tags"].split(",")) {
                foetus.updateTag("tags", tag)
            }
        }

        info.nz("weak") {
            for (weakness in info["weak"].split(",")) {
            foetus.updateTag("weakness", weakness)
            }
        }

        info.nz("strong") {
            for (resistance in info["strong"].split(",")) {
                foetus.updateTag("resistance", resistance)
            }
        }
        foetus.updateTag("tags", "creature")

        val toStart: Coord
        val toMap: String

        if (pos == null) {
            toMap = mapID ?: GameStore.curMap.id
            toStart = start ?: GameStore.mapList[toMap]!!.randomFloor()
        } else {
            toMap = mapID ?: pos["mapID"]
            toStart = start ?: Coord.get(pos["x"].toInt(), pos["y"].toInt())
        }

        foetus.addPos(toStart, toMap)
        info.nz("vision") {
            val m = GameStore.getMapByID(toMap)
            foetus.addVision(info["vision"].toDouble())
            foetus.vision!!.visible = ArrayTools.fill(0.0, m.width, m.height)
            FOV.reuseFOV(m.resistances, foetus.vision!!.visible, toStart.x, toStart.y, foetus.vision!!.vision)
        }
        foetus.blocking = true

        info.nz("ai") { foetus.addAI(foetus.movDly, info["ai"]) }
        info.nz("equip") {
            info.getChildByName("equip").attributes.values().forEach {
                val item = ItemBuilder.build(it, toMap)
                foetus.putOn(item)
            }
        }

        GameStore.addEntity(foetus)
        if (foetus.hasTag("leader")) Faction.addFaction(foetus.eID)
        return foetus
    }
}

