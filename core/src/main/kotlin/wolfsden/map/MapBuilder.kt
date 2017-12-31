package wolfsden.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidgrid.mapping.SectionDungeonGenerator
import squidpony.squidgrid.mapping.SerpentMapGenerator
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

        val smg = SerpentMapGenerator(width, height, WolfRNG.wolfRNG)
        val deco = SectionDungeonGenerator(width, height, WolfRNG.wolfRNG)

        smg.putBoxRoomCarvers(info["boxCarvers"].toInt())
        smg.putCaveCarvers(info["caveCarvers"].toInt())
        smg.putRoundRoomCarvers(info["roomCarvers"].toInt())

        val raw = smg.generate()
        deco.addDoors(info["doors"].toInt(), info["doubleDoors"].toBoolean() ?: false)
        if (info.hasAttribute("water")) deco.addLake(info["water"].toInt())
        val baseMap = deco.generate(raw, smg.environment)
        val finished = WolfMap(mapID, info["name"], baseMap, info["light"].toBoolean())

        GameStore.mapList[mapID] = finished

        return finished
    }

}