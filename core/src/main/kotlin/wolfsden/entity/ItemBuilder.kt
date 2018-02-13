package wolfsden.entity

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import squidpony.squidgrid.mapping.RoomFinder
import squidpony.squidmath.Coord
import squidpony.squidmath.ProbabilityTable
import wolfsden.log
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG
import java.util.*

private interface BaseMarker {
    val id: String
    val rarity: Int
}

data class EquipBase(
    override val id: String,
    val name: String = "No name",
    val desc: String = "No description",
    val glyph: String = "@",
    val color: String = "White",
    override val rarity: Int = 0,
    val slot: String = "mh",
    val atk: Int = 0,
    val dmg: Int = 0,
    val dly: Int = 0,
    val sav: Int = 0,
    val dfp: Int = 0,
    val prot: Int = 0,
    val tags: List<String> = listOf()
) : BaseMarker

data class ItemBase(
    override val id: String,
    val name: String = "No name",
    val desc: String = "No description",
    val glyph: String = "@",
    val color: String = "White",
    override val rarity: Int = 0,
    val itemType: String = "healing",
    val pctAmt: Float = 0f,
    val flatAmt: Int = 0
) : BaseMarker

object ItemBuilder {
    private const val eqFile = "data/entity/equipment.yml"
    private const val itemFile = "data/entity/items.yml"
    private val mapper = ObjectMapper(YAMLFactory())
    init {mapper.registerModule(KotlinModule())}
    private val eqBP: List<EquipBase> = mapper.readValue(Gdx.files.internal(eqFile).reader())
    private val itemBP: List<ItemBase> = mapper.readValue(Gdx.files.internal(itemFile).reader())

    fun buildEquip(buildID: String): Entity {
        require(eqBP.any { it.id == buildID }, { "$buildID is not a valid equipment ID" })
        val info = eqBP.first { it.id == buildID }
        val mold = Entity(UUID.randomUUID().toString())

        val toGlyph = if (info.glyph.length == 1) {
            info.glyph.first()
        } else {
            info.glyph.toInt(16).toChar()
        }


        mold.addID(info.name, info.desc)
        mold.addDraw(toGlyph, info.color, 1)

        val slot = when (info.slot) {
            "2H" -> Slot.TWOH
            "ambi" -> Slot.AMBI
            "mh" -> Slot.MH
            "oh" -> Slot.OH
            "armor" -> Slot.ARMOR
            else -> Slot.TRINKET
        }

        mold.addEQ(slot, info.atk, info.dfp, info.dmg, info.sav, info.dly, info.prot)

        info.tags.forEach {
            mold.updateTag(it)
        }

        mold.updateTag("equipment")

        mold.blocking = false
        GameStore.addEntity(mold)
        return mold
    }

    fun buildItem(buildID: String): Entity {
        require(itemBP.any { it.id == buildID }, { "$buildID is not a valid item ID" })
        val info = itemBP.first { it.id == buildID }
        val mold = Entity(UUID.randomUUID().toString())
        val toGlyph = if (info.glyph.length == 1) {
            info.glyph.first()
        } else {
            info.glyph.toInt(16).toChar()
        }

        mold.addID(info.name, info.desc)
        mold.addDraw(toGlyph, info.color, 1)

        when (info.itemType) {
            "healing" -> mold.addHeal(info.flatAmt, info.pctAmt)
            "repair" -> mold.addRestore(info.flatAmt, info.pctAmt)
            else -> mold.addRepair(info.flatAmt, info.pctAmt)
        }

        mold.updateTag("item")

        mold.blocking = false
        GameStore.addEntity(mold)
        return mold
    }

    fun seedItems(mapID: String, maxItems: Int = 10, vararg tags: String) {
        val numItems = WolfRNG.wolfRNG.nextInt(maxItems)
        val table: ProbabilityTable<BaseMarker> = ProbabilityTable(WolfRNG.wolfRNG)
        val itemCands: Set<BaseMarker> = itemBP.filter { it.rarity > 0 }.toSet()
        val eqCands: Set<BaseMarker> = eqBP.filter { it.rarity > 0 }.toSet()

        val coll: Set<BaseMarker> = if (tags.isEmpty()) {
            eqCands union itemCands
        } else {
            eqCands.filter { (it as EquipBase).tags.any { tags.contains(it) } } union itemCands
        }
        for (chosen in coll) {
            table.add(chosen, chosen.rarity)
        }

        for (i in 0.until(numItems)) {
            val randItem = table.random()
            when (randItem) {
                is EquipBase -> seed(buildEquip(randItem.id), mapID, roomsOnly = true)
                is ItemBase ->seed(buildItem(randItem.id), mapID, roomsOnly = true)
                else -> log(0, "ItemBuilder", "Attempt to seed invalid item ${randItem.id}")
            }
        }
    }
}