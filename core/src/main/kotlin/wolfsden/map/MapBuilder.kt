package wolfsden.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidgrid.mapping.MixedGenerator
import squidpony.squidgrid.mapping.SectionDungeonGenerator
import wolfsden.nz
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG

object MapBuilder {
    const val mapFile = "data/maps.xml"
    val reader = XmlReader()
    val mapBP = reader.parse(Gdx.files.internal(mapFile))

    fun build(mapID: String): WolfMap {
        val info = mapBP.getChildrenByName("WolfMap").filter { it["id"] == mapID }.first()
        val width = info["width"].toInt()
        val height = info["height"].toInt()

        val smg = MixedGenerator(width, height, WolfRNG.wolfRNG)
        val deco = SectionDungeonGenerator(width, height, WolfRNG.wolfRNG)

        info.nz("boxCarvers") { smg.putBoxRoomCarvers(info["boxCarvers"].toInt()) }
        info.nz("caveCarvers") { smg.putCaveCarvers(info["caveCarvers"].toInt()) }
        info.nz("roomCarvers") { smg.putRoundRoomCarvers(info["roomCarvers"].toInt()) }

        val raw = smg.generate()
        info.nz("doors") { deco.addDoors(info["doors"].toInt(), info["doors"].toBoolean()) }
        info.nz("water") { deco.addLake(info["water"].toInt()) }
        val baseMap = deco.generate(raw, smg.environment)
        val finished = WolfMap(mapID, info["name"], baseMap, info["light"].toBoolean())

        GameStore.mapList[mapID] = finished

        return finished
    }

}