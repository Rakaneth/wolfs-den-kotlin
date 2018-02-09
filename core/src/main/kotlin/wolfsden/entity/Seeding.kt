package wolfsden.entity

import squidpony.ArrayTools
import squidpony.squidgrid.FOV
import squidpony.squidgrid.mapping.RoomFinder
import squidpony.squidmath.Coord
import wolfsden.system.GameStore
import wolfsden.system.WolfRNG

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

    GameStore.addEntity(entity)
}