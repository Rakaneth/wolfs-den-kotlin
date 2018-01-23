package wolfsden.system

import squidpony.squidai.DijkstraMap
import wolfsden.entity.Entity
import wolfsden.system.GameStore.curEntities

fun Entity.isAlly(other: Entity): Boolean = other.eID == this.ai?.leader || other.ai?.leader == this.ai?.leader
fun Entity.isEnemy(other: Entity): Boolean = !this.isAlly(other) && other.hasTag("creature")
fun Entity.allies() = curEntities.filter { this.isAlly(it) && it != this }
fun Entity.visibleAllies() = this.allies().filter { this.visible(it) && it.vit!!.alive }
fun Entity.visibleEnemies() = curEntities.filter { this.visible(it) && this.isEnemy(it) && it.vit!!.alive }
fun Entity.isLeader() = Faction.dMaps.containsKey(this.eID)

object Faction : EntityListener {
    val dMaps: MutableMap<String, DijkstraMap> = mutableMapOf()

    fun addFaction(leader: String) {
        val eLeader = GameStore.getByID(leader)
        val eMap = GameStore.getMapByID(eLeader!!.pos!!.mapID)
        dMaps[leader] = DijkstraMap(eMap.baseMap)
    }

    fun removeFaction(leader: String) {
        dMaps.remove(leader)
    }

    fun getDMap(leader: String): DijkstraMap {
        require(dMaps.containsKey(leader), { "${GameStore.getByID(leader)} has no faction to lead" })
        return dMaps[leader]!!
    }

    init {
        GameStore.addListener(this)
    }

    override fun onRemove(entity: Entity) {
        if (entity.isLeader()) {
            removeFaction(entity.eID)
            entity.allies().forEach {
                it.ai!!.leader = it.eID
                addFaction(it.eID)
                //TODO: make lackeys afraid of alpha's killer?
            }
        }
    }
}