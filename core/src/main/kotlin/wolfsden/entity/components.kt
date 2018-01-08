package wolfsden.entity

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager
import squidpony.squidmath.Coord
import wolfsden.system.GameStore
import java.io.Serializable

enum class Slot { MH, OH, ARMOR, TRINKET, TWOH, AMBI }

open class Component(open val entity: String) : Serializable {
    val getEntity
        get() = GameStore.getByID(entity)!!
}

interface Item {
    fun use(user: Entity? = null, target: Coord? = null)
}

data class Identity(
        override val entity: String,
        val name: String = "No Name",
        val desc: String = "No description"
) : Component(entity)

data class Stats(
        override val entity: String,
        var str: Int = 1,
        var stam: Int = 1,
        var spd: Int = 1,
        var skl: Int = 1
) : Component(entity)

data class Vitals(
        override val entity: String,
        var alive: Boolean = true,
        var curVit: Int = 0,
        var maxVit: Int = 0,
        var curEnd: Int = 0,
        var maxEnd: Int = 0
) : Component(entity)

data class Drawing(
        override val entity: String,
        val glyph: Char = '@',
        val color: String = "White",
        val layer: Int = 1
) : Component(entity)

data class Position(
        override val entity: String,
        var mapID: String,
        var x: Int = -1,
        var y: Int = -1
) : Component(entity) {
    val coord: Coord
        get() = Coord.get(x, y)
}

data class EquipStats(
        override val entity: String,
        val slot: Slot,
        val atk: Int = 0,
        val dfp: Int = 0,
        val dmg: Int = 0,
        val sav: Int = 0,
        val dly: Int = 0,
        var curProt: Int = 0,
        val prot: Int = 0
) : Component(entity) {
    val name
        get() = getEntity.id!!.name
    val desc
        get() = getEntity.id!!.desc
}

class HealingItem(
        override val entity: String,
        private val pctAmt: Float = 0f,
        private val flatAmt: Int = 0
) : Component(entity), Item {
    override fun use(user: Entity?, target: Coord?) {
        user?.heal(flatAmt, pctAmt)
    }
}

class RepairItem(
        override val entity: String,
        private val pctAmt: Float = 0f,
        private val flatAmt: Int = 0
) : Component(entity), Item {
    override fun use(user: Entity?, target: Coord?) {
        user?.repair(flatAmt, pctAmt)
    }
}

class RestoreItem(
        override val entity: String,
        private val pctAmt: Float = 0f,
        private val flatAmt: Int = 0
) : Component(entity), Item {
    override fun use(user: Entity?, target: Coord?) {
        user?.restore(flatAmt, pctAmt)
    }
}

data class XPGainer(
        override val entity: String,
        var curXP: Float = 0f,
        var totXP: Float = 0f
) : Component(entity)

data class Vision(
        override val entity: String,
        var vision: Double = 6.0
) : Component(entity) {
    var visible: Array<DoubleArray>? = null
}

class AI(
        override val entity: String,
        var delay: Int,
        private var aiTree: String
) : Component(entity) {
    @Transient
    var btree: BehaviorTree<Entity>? = null
    var target: String? = null
    var leader: String = entity

    fun updateBTree(aiName: String? = null) {
        if (aiName != null) aiTree = "data/ai/$aiName.tree"
        btree = BehaviorTreeLibraryManager.getInstance().library.createBehaviorTree(aiTree, GameStore.entityList[entity])
    }

    fun getBTree(): BehaviorTree<Entity> {
        return if (btree == null) {
            updateBTree()
            btree!!
        } else {
            btree!!
        }
    }

    fun getTarget(): Entity? = GameStore.entityList[target]
}

class EffectStack(
        override val entity: String
) : Component(entity) {
    val effects: MutableList<Effect> = mutableListOf()
    val atk
        get() = effects.sumBy { it.atk }
    val dfp
        get() = effects.sumBy { it.dfp }
    val dmg
        get() = effects.sumBy { it.dmg }
    val sav
        get() = effects.sumBy { it.sav }
    val curProt
        get() = effects.sumBy { it.curProt }
    val atkDly
        get() = effects.sumBy { it.atkDly }
    val movDly
        get() = effects.sumBy { it.movDly }
    val loseTurn
        get() = effects.any { it.loseTurn }
}
