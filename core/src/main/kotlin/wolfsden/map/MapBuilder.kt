package wolfsden.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidgrid.mapping.MixedGenerator
import squidpony.squidgrid.mapping.SectionDungeonGenerator
import wolfsden.log
import wolfsden.nz
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG

object MapBuilder {
    private const val mapFile = "data/maps.xml"
    private val reader = XmlReader()
    private val mapBP = reader.parse(Gdx.files.internal(mapFile))

    fun build(mapID: String): WolfMap {
        val info = mapBP.getChildrenByName("WolfMap").first { it["id"] == mapID }
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

    fun buildAll() {
        for (node in mapBP.getChildrenByName("WolfMap")) {
            val buildID = node["id"]
            if (!GameStore.mapList.containsKey(buildID)) build(buildID)
            val curMap = GameStore.getMapByID(buildID)
            node.nz("connections") {
                node.getChildByName("connections").getChildrenByName("connection").forEach {
                    val connID = it["mapID"]
                    if (!GameStore.mapList.containsKey(connID)) build(connID)
                    val fromC = curMap.randomFloor()
                    val toMap = GameStore.mapList[connID]!!
                    val toC = toMap.randomFloor()
                    val twoWay = it["twoWay"].toBoolean()
                    when (it["direction"]) {
                        "up" -> {
                            curMap.upStair(fromC)
                            if (twoWay) toMap.downStair(toC)
                        }
                        "down" -> {
                            curMap.downStair(fromC)
                            if (twoWay) toMap.upStair(fromC)
                        }
                        else -> {
                        } //TODO: outstair
                    }
                    curMap.connect(fromC, toC, toMap.id)
                    if (twoWay) toMap.connect(toC, fromC, curMap.id)
                    log(0, "Map Generation", "Connecting $buildID to $connID")
                }
            }
        }
    }
}

