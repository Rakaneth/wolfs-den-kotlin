package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.module.kotlin.readValue
import squidpony.squidmath.Coord
import wolfsden.mapper
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG
import java.io.FileInputStream
import java.util.*

data class CreatureBase(
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
    val tags: List<String> = listOf(),
    val gainsXP: Boolean = false,
    val xp: Float = 0f,
    val inventory: Int = 10,
    val rarity: Int = 0,
    val skills: List<String> = listOf()
)

object CreatureBuilder {
    private const val creatureFile = "data/entity/creatures.yml"
    lateinit var creatureBP: List<CreatureBase>

    fun initBP(live: Boolean = true) {
        val theReader = if (live) {
            Gdx.files.internal(creatureFile).reader()
        } else {
            FileInputStream("src/test/res/$creatureFile").reader()
        }

        creatureBP = mapper.readValue(theReader)
    }

    fun build(buildID: String, isPlayer: Boolean = false, name: String? = null): Entity {
        val info = creatureBP.first { it.id == buildID }
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
            foetus.updateTag(it)
        }

        foetus.updateTag("creature")
        foetus.addVision(info.vision)
        foetus.blocking = true

        foetus.addAI(foetus.movDly, info.ai)

        listOf(info.mh, info.oh, info.armor, info.trinket).filter { it != "none" }.forEach {
            val item = ItemBuilder.buildEquip(it)
            foetus.putOn(item)
        }

        foetus.isPlayer = isPlayer
        foetus.addInventory(info.inventory)
        foetus.addEffect()
        foetus.addAggro()

        GameStore.addEntity(foetus)

        info.skills.forEach {
            foetus.learnSkill(it).resetCD()
        }

        return foetus
    }

    fun buildAndSeed(buildID: String, isPlayer: Boolean = false, name: String? = null, mapID: String? = null,
                     start: Coord? = null): Entity {
        val creature = build(buildID, isPlayer, name)
        seed(creature, mapID, start)
        return creature
    }

    fun buildPack(lackeyID: String, leaderID: String, numLackeys: Int, mapID: String = GameStore.curMap.id,
                  leaderStart: Coord? = null) {
        val leader = build(leaderID)
        seed(leader, mapID, leaderStart)
        for (i in 0 until numLackeys) {
            val startC = GameStore.getMapByID(mapID).randomFloorWithin(leader.pos!!.coord, 2)
            val lackey = build(lackeyID)
            seed(lackey, mapID, startC)
            lackey.ai!!.leader = leader.eID
        }
    }

    fun buildWolfPack(mapID: String = GameStore.curMap.id) {
        buildPack("wolf", "alpha", WolfRNG.wolfRNG.between(2, 5), mapID)
    }

    fun buildGreaterPack(mapID: String = GameStore.curMap.id) {
        buildPack("dire", "direAlpha", WolfRNG.wolfRNG.between(2, 5), mapID)
    }
}

