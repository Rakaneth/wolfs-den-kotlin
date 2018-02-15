package wolfsden.system

import squidpony.squidai.DijkstraMap
import wolfsden.entity.Entity
import wolfsden.system.GameStore.curEntities
import wolfsden.system.GameStore.player

fun Entity.isAlly(other: Entity): Boolean {
    val isPackmate = other.eID == this.ai?.leader || other.ai?.leader == this.ai?.leader
    val isInAllyList = other.tags.any { this.factionStack!!.ally.contains(it)}
    return isPackmate || isInAllyList
}

fun Entity.isEnemy(other: Entity): Boolean {
    return !this.isAlly(other)
           && other.hasTag("creature")
           && other.tags.any { this.factionStack!!.hostile.contains(it)}
}

fun Entity.allies() = curEntities.filter { this.isAlly(it) && it != this }
fun Entity.visibleAllies() = this.allies().filter { this.visible(it) && it.vit!!.alive }
fun Entity.visibleEnemies() = if (Location.sameMap(player, this)) curEntities.filter {
    this.visible(it) && this.isEnemy(it) && it.vit!!.alive
} else emptyList()

fun Entity.isLeader() = Faction.dMaps.containsKey(this.eID)
fun Entity.addAlly(factionTag: String) {
    with (factionStack!!) {
        listOf(neutral, hostile).forEach {
            it.remove(factionTag)
        }
        ally.add(factionTag)
    }
}
fun Entity.addNeutral(factionTag: String) {
    with (factionStack!!) {
        listOf(hostile, ally).forEach {
            it.remove(factionTag)
        }
        neutral.add(factionTag)
    }
}
fun Entity.addHostile(factionTag: String) {
    with (factionStack!!) {
        listOf(neutral, ally).forEach {
            it.remove(factionTag)
        }
        hostile.add(factionTag)
    }
}

object Faction : EntityListener {
    var dMaps: MutableMap<String, DijkstraMap> = mutableMapOf()

    fun addFaction(leader: String) {
        val eLeader = GameStore.getByID(leader)
        val eMap = GameStore.getMapByID(eLeader!!.pos!!.mapID)
        dMaps[leader] = DijkstraMap(eMap.baseMap)
    }

    fun removeFaction(leader: String) {
        dMaps.remove(leader)
    }

    fun getDMap(leader: String): DijkstraMap {
        //require(dMaps.containsKey(leader), { "${GameStore.getByID(leader)} has no faction to lead" })
        return if (dMaps.containsKey(leader)) {
            dMaps[leader]!!
        } else {
            val realLeader = GameStore.getByID(leader)!!.ai!!.leader
            dMaps[realLeader]!!
        }
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
