package wolfsden.entity

import squidpony.ArrayTools
import squidpony.squidgrid.FOV
import squidpony.squidgrid.mapping.RoomFinder
import squidpony.squidmath.Coord
import wolfsden.system.*

fun seed(entity: Entity, mapID: String?, start: Coord? = null, roomsOnly: Boolean = false) {
    val toMap = mapID ?: GameStore.curMap.id
    val wMap = GameStore.getMapByID(toMap)
    val toStart = start ?: if (roomsOnly) {
        RoomFinder(wMap.baseMap).allRooms.singleRandom(WolfRNG.wolfRNG)
    } else {
        wMap.randomFloor()
    }

    entity.addPos(toStart, toMap)

    if (entity.vision != null) {
        entity.vision!!.visible = ArrayTools.fill(0.0, wMap.width, wMap.height)
        FOV.reuseFOV(wMap.resistances, entity.vision!!.visible, toStart.x, toStart.y, entity.vision!!.vision)
    }

    entity.skillStack?.skills?.forEach {
        it.setMap(entity.getMap().baseMap)
    }
    if (entity.hasTag("leader") || entity.hasTag("solo")) Faction.addFaction(entity.eID)

    GameStore.addEntity(entity)
}