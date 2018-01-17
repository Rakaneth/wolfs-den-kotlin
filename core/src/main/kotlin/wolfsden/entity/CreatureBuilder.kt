package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.XmlReader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import squidpony.ArrayTools
import squidpony.squidgrid.FOV
import squidpony.squidmath.Coord
import wolfsden.nz
import wolfsden.system.Faction
import wolfsden.system.GameStore
import java.util.*

data class CreatureBase (
        val id: String,
        val name: String = "No name",
        val desc: String = "No description",
        val glyph: String = "@",
        val color: String = "White",
        val str: Int = 1,
        val stam: Int = 1,
        val spd: Int = 1,
        val skl: Int = 1,
        val mh: String = "none",
        val oh: String = "none",
        val armor: String = "none",
        val trinket: String = "none",
        val vision: Double = 6.0,
        val ai: String = "hunt",
        val weakness: List<String> = listOf(),
        val resistance: List<String> = listOf(),
        val tags: List<String> = listOf(),
        val gainsXP: Boolean = false,
        val xp: Float = 0f,
        val inventory: Int = 10,
        val rarity: Int = 0
)

object CreatureBuilder {
    //private val reader = XmlReader()
    //private const val creatureFile = "data/monsters.xml"
    private const val creatureFile =  "data/entity/base.creature.json"
    //private val creatureBP: XmlReader.Element = reader.parse(Gdx.files.internal(creatureFile))
    lateinit var creatureBP: List<CreatureBase>

    init {
        val mapper = jacksonObjectMapper()
        creatureBP = mapper.readValue(Gdx.files.internal(creatureFile).reader())
        println(creatureBP.size)
    }


    fun build(buildID: String, isPlayer: Boolean = false, start: Coord? = null, mapID: String? = null, name: String? = null): Entity? {
        val info = creatureBP.first { it.id == buildID}
        val eID = if (isPlayer) "player" else UUID.randomUUID().toString()
        val foetus = Entity(eID)
        val toName = name ?: info.name
        val glyph = if (info.glyph.length > 1) info.glyph.toInt(16).toChar() else info.glyph.first()

        foetus.addID(toName, info.desc)
        foetus.addDraw(glyph, info.color, 2)
        foetus.addStats(info.str, info.stam, info.spd, info.skl)

        foetus.addVitals(true, foetus.maxVit, foetus.maxVit, foetus.maxEnd, foetus.maxEnd)
        if (info.gainsXP) foetus.addXP(0f, 0f)
        foetus.worthXP = info.xp

        info.tags.forEach {
            foetus.updateTag("tags", it)
        }

        info.weakness.forEach {
            foetus.updateTag("weakness", it)
        }

        info.resistance.forEach {
            foetus.updateTag("resistance", it)
        }

        foetus.updateTag("tags", "creature")
        val toMap = mapID ?: GameStore.curMap.id
        val toStart = start ?: GameStore.mapList[toMap]!!.randomFloor()

        foetus.addPos(toStart, toMap)

        val m = GameStore.getMapByID(toMap)
        foetus.addVision(info.vision)
        foetus.vision!!.visible = ArrayTools.fill(0.0, m.width, m.height)
        FOV.reuseFOV(m.resistances, foetus.vision!!.visible, toStart.x, toStart.y, foetus.vision!!.vision)

        foetus.blocking = true

        foetus.addAI(foetus.movDly, info.ai)

        listOf(info.mh, info.oh, info.armor, info.trinket).filter{ it != "none" }.forEach {
            val item = ItemBuilder.build(it, toMap)
            foetus.putOn(item)
        }

        foetus.isPlayer = isPlayer
        foetus.addInventory(info.inventory)
        foetus.addEffect()

        GameStore.addEntity(foetus)
        if (foetus.hasTag("leader") || foetus.hasTag("solo")) Faction.addFaction(foetus.eID)
        return foetus
    }
}

