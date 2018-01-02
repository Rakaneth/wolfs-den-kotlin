package wolfsden.system

import squidpony.squidmath.StatefulRNG
import wolfsden.entity.Entity
import wolfsden.map.WolfMap
import wolfsden.system.GameStore.player
import java.io.*

fun Entity.playerVisible(): Boolean = player.visible(this)

interface EntityListener {
    fun onAdd(entity: Entity)
    fun onRemove(entity: Entity)
}

object GameStore {
    var entityList: MutableMap<String, Entity> = mutableMapOf()
    var mapList: MutableMap<String, WolfMap> = mutableMapOf()
    var mapDirty = true
    var hudDirty = true
    private val listeners: MutableList<EntityListener> = mutableListOf()

    val player
        get() = entityList["player"]!!

    val curMap
        get() = mapList[player.pos!!.mapID]!!

    val allEntities
        get() = entityList.values

    val curEntities
        get() = allEntities.filter { Location.sameMap(it, player) }

    fun addListener(listener: EntityListener) { listeners.add(listener)}
    fun removeListener(listener: EntityListener) { listeners.remove(listener)}

    fun update(map: Boolean = true, hud: Boolean = true) {
        if (map) mapDirty = true
        if (hud) hudDirty = true
    }

    fun addEntity(entity: Entity) {
        entityList[entity.eID] = entity
        for (listener in listeners) { listener.onAdd(entity) }
    }

    fun removeEntity(entity: Entity) {
        entityList.remove(entity.eID)
        for (listener in listeners) { listener.onRemove(entity)}
    }

    fun getByID(eID: String): Entity? = entityList[eID]
    fun getMapByID(mapID: String): WolfMap = mapList[mapID]!!


    fun saveGame() {
        val savePath = "${System.getProperty("user.home")}/WolfsDenKotlin"
        val fileName = player.id!!.name + ".wlf"
        try {
            val dir = File(savePath)
            if (!dir.exists() || !dir.isDirectory) {
                File(savePath).mkdir()
            }
            ObjectOutputStream(FileOutputStream("$savePath/$fileName")).use { it ->
                it.writeObject(entityList)
                it.writeObject(mapList)
                it.writeObject(WolfRNG.wolfRNG)
            }
            println("Game saved")
        } catch (e: IOException) {
            println("Error saving game: ${e.stackTrace}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun loadGame(fileName: String) {
        val filePath = "${System.getProperty("user.home")}/WolfsDenKotlin/$fileName"
        try {
            ObjectInputStream(FileInputStream(filePath)).use { it ->
                val entityBlob = it.readObject()
                val mapBlob = it.readObject()
                val rngBlob = it.readObject()

                when (entityBlob) {
                    is MutableMap<*, *> ->
                        entityList = entityBlob as MutableMap<String, Entity>
                    else -> throw IOException("Error loading entity table")
                }

                when (mapBlob) {
                    is MutableMap<*, *> -> mapList = mapBlob as MutableMap<String, WolfMap>
                    else -> throw IOException("Error loading map table")
                }

                when (rngBlob) {
                    is StatefulRNG -> WolfRNG.wolfRNG = rngBlob
                    else -> throw IOException("Error loading RNG")
                }
            }
            println("$fileName loaded")
        } catch (e: IOException) {
            println("Error loading $fileName: ${e.stackTrace}")
        }
    }

    fun newGame(playerClass: String, playerName: String) {

    }
}