package wolfsden.map

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import squidpony.squidgrid.mapping.SectionDungeonGenerator
import squidpony.squidgrid.mapping.SerpentMapGenerator
import wolfsden.log
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG


data class MapBase(
    val id: String,
    val name: String = "No name",
    val light: Boolean = false,
    val width: Int = 20,
    val height: Int = 20,
    val boxCarvers: Int = 0,
    val roomCarvers: Int = 0,
    val caveCarvers: Int = 0,
    val doors: Int = 0,
    val doubleDoors: Boolean = true,
    val water: Int = 0,
    val connections: List<ConnectionBase> = listOf()
)

data class ConnectionBase(
    val mapID: String,
    val direction: String = "down",
    val twoWay: Boolean = false
)

object MapBuilder {
    private const val mapFile = "data/maps.yml"
    private val mapper =  ObjectMapper(YAMLFactory())
    init { mapper.registerModule(KotlinModule())}
    private val mapBP: List<MapBase> = mapper.readValue(Gdx.files.internal(mapFile).reader())

    fun build(mapID: String): WolfMap {
        require(mapBP.any { it.id == mapID }, { "$mapID is not a valid map ID" })
        val info = mapBP.first { it.id == mapID }

        val smg = SerpentMapGenerator(info.width, info.height, WolfRNG.wolfRNG, 0.2)
        val deco = SectionDungeonGenerator(info.width, info.height, WolfRNG.wolfRNG)

        smg.putBoxRoomCarvers(info.boxCarvers)
        smg.putCaveCarvers(info.caveCarvers)
        smg.putRoundRoomCarvers(info.roomCarvers)

        val raw = smg.generate()
        deco.addDoors(info.doors, info.doubleDoors)
        deco.addLake(info.water)
        val baseMap = deco.generate(raw, smg.environment)
        val finished = WolfMap(mapID, info.name, baseMap, info.light)

        GameStore.mapList[mapID] = finished

        return finished
    }

    fun buildAll() {
        for (node in mapBP) {
            val buildID = node.id
            if (!GameStore.mapList.containsKey(buildID)) build(buildID)
            val curMap = GameStore.getMapByID(buildID)

            node.connections.forEach {
                val connID = it.mapID
                if (!GameStore.mapList.containsKey(connID)) build(connID)
                val fromC = curMap.randomFloor()
                val toMap = GameStore.mapList[connID]!!
                val toC = toMap.randomFloor()
                val twoWay = it.twoWay
                when (it.direction) {
                    "up" -> {
                        curMap.upStair(fromC, !twoWay)
                        if (twoWay) toMap.downStair(toC)
                    }
                    "down" -> {
                        curMap.downStair(fromC, !twoWay)
                        if (twoWay) toMap.upStair(toC)
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

