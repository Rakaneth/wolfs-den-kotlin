package wolfsden.system

import wolfsden.entity.Entity

fun onDeath(entity: Entity) {
    when (entity.id!!.name) {
        "Wolflord" -> {
            DialogManager.startDialog("defeatedWolflord")
            entity.grantXP()
        }
        "Margrave of Bones" -> {
            DialogManager.startDialog("defeatedMargrave")
        }
        else -> {
            entity.grantXP()
        }
    }
    GameStore.update(false, true)
}

private fun Entity.grantXP() {
    this.aggroStack!!.aggroList.keys.forEach {
        val killer = GameStore.getByID(it)
        killer!!.xp?.gainXP(this.worthXP)
    }
}